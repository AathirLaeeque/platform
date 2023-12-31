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

package org.gradle.kotlin.dsl.support.bytecode

import org.gradle.api.JavaVersion
import org.objectweb.asm.Opcodes


object GradleJvmVersion {

    /**
     * The minimal Java version required to run Gradle.
     */
    val minimalJavaVersion: JavaVersion =
        JavaVersion.VERSION_1_8

    /**
     * The class file version for the minimal Java version required to run Gradle.
     */
    val minimalAsmClassVersion: Int =
        Opcodes.V1_8
}
