/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.initialization.exception;

import org.gradle.api.GradleScriptException;
import org.gradle.api.ProjectConfigurationException;
import org.gradle.api.internal.initialization.loadercache.ClassLoaderId;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.groovy.scripts.ScriptCompilationException;
import org.gradle.initialization.ClassLoaderScopeId;
import org.gradle.initialization.ClassLoaderScopeOrigin;
import org.gradle.initialization.ClassLoaderScopeRegistryListener;
import org.gradle.initialization.ClassLoaderScopeRegistryListenerManager;
import org.gradle.internal.classpath.ClassPath;
import org.gradle.internal.exceptions.Contextual;
import org.gradle.internal.exceptions.LocationAwareException;
import org.gradle.internal.hash.HashCode;
import org.gradle.internal.service.ServiceCreationException;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultExceptionAnalyser implements ExceptionCollector, ClassLoaderScopeRegistryListener, Closeable {
    private final Map<String, String> scripts = new HashMap<>();
    private final ClassLoaderScopeRegistryListenerManager listenerManager;

    public DefaultExceptionAnalyser(@Nullable ClassLoaderScopeRegistryListenerManager listenerManager) {
        this.listenerManager = listenerManager;
        if (listenerManager != null) {
            listenerManager.add(this);
        }
    }

    @Override
    public void close() throws IOException {
        if (listenerManager != null) {
            listenerManager.remove(this);
        }
    }

    @Override
    public void childScopeCreated(ClassLoaderScopeId parentId, ClassLoaderScopeId childId, @Nullable ClassLoaderScopeOrigin origin) {
        if (origin instanceof ClassLoaderScopeOrigin.Script) {
            ClassLoaderScopeOrigin.Script scriptOrigin = (ClassLoaderScopeOrigin.Script) origin;
            scripts.put(scriptOrigin.getFileName(), scriptOrigin.getDisplayName());
        }
    }

    @Override
    public void classloaderCreated(ClassLoaderScopeId scopeId, ClassLoaderId classLoaderId, ClassLoader classLoader, ClassPath classPath, @Nullable HashCode implementationHash) {
    }

    @Override
    public void collectFailures(Throwable exception, Collection<? super Throwable> failures) {
        if (exception instanceof ProjectConfigurationException) {
            ProjectConfigurationException projectConfigurationException = (ProjectConfigurationException) exception;
            List<Throwable> additionalFailures = new ArrayList<>();
            for (Throwable cause : projectConfigurationException.getCauses()) {
                // TODO: remove this special case
                if (cause instanceof GradleScriptException) {
                    failures.add(transform(cause));
                } else {
                    additionalFailures.add(cause);
                }
            }
            if (!additionalFailures.isEmpty()) {
                projectConfigurationException.initCauses(additionalFailures);
                failures.add(transform(projectConfigurationException));
            }
        } else if (exception instanceof ServiceCreationException) {
            failures.add(transform(new InitializationException(exception)));
        } else {
            failures.add(transform(exception));
        }
    }

    private Throwable transform(Throwable exception) {
        Throwable actualException = findDeepestRootException(exception);
        if (actualException instanceof LocationAwareException) {
            return actualException;
        }

        String source = null;
        Integer lineNumber = null;

        // TODO: remove these special cases
        if (actualException instanceof ScriptCompilationException) {
            ScriptCompilationException scriptCompilationException = (ScriptCompilationException) actualException;
            source = scriptCompilationException.getScriptSource().getDisplayName();
            lineNumber = scriptCompilationException.getLineNumber();
        }

        if (source == null) {
            for (
                Throwable currentException = actualException;
                currentException != null;
                currentException = currentException.getCause()
            ) {
                for (StackTraceElement element : currentException.getStackTrace()) {
                    if (element.getLineNumber() >= 0 && scripts.containsKey(element.getFileName())) {
                        source = scripts.get(element.getFileName());
                        lineNumber = element.getLineNumber();
                        break;
                    }
                }
            }
        }

        return new LocationAwareException(actualException, source, lineNumber);
    }

    private Throwable findDeepestRootException(Throwable exception) {
        // TODO: fix the way we work out which exception is important: TaskExecutionException is not always the most helpful
        Throwable locationAware = null;
        Throwable result = null;
        Throwable contextMatch = null;
        for (Throwable current = exception; current != null; current = current.getCause()) {
            if (current instanceof LocationAwareException) {
                locationAware = current;
            } else if (current instanceof GradleScriptException || current instanceof TaskExecutionException) {
                result = current;
            } else if (contextMatch == null && current.getClass().getAnnotation(Contextual.class) != null) {
                contextMatch = current;
            }
        }
        if (locationAware != null) {
            return locationAware;
        } else if (result != null) {
            return result;
        } else if (contextMatch != null) {
            return contextMatch;
        } else {
            return exception;
        }
    }
}
