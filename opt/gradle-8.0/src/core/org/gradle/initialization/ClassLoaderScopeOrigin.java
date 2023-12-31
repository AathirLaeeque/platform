/*
 * Copyright 2022 the original author or authors.
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

package org.gradle.initialization;

/**
 * Details about the origin of the contents of the ClassLoader scope.
 */
public interface ClassLoaderScopeOrigin {
    class Script implements ClassLoaderScopeOrigin {
        private final String fileName;
        private final String displayName;

        public Script(String fileName, String displayName) {
            this.fileName = fileName;
            this.displayName = displayName;
        }

        public String getFileName() {
            return fileName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public int hashCode() {
            return fileName.hashCode() ^ displayName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            Script other = (Script) obj;
            return fileName.equals(other.fileName) && displayName.equals(other.displayName);
        }
    }
}
