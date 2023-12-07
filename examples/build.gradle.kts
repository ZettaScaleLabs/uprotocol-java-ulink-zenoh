//
// Copyright (c) 2023 ZettaScale Technology
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License 2.0 which is available at
// http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
//
// Contributors:
//   ZettaScale Zenoh Team, <zenoh@zettascale.tech>
//

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    // Use Maven Local
    mavenLocal()
}

dependencies {
    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")
    // uprotocol-java
    implementation("org.eclipse.uprotocol:uprotocol-java:1.5.4")
    // uprotocol-java-ulink-zenoh
    implementation(project(":lib"))
    // protobuf
    implementation("com.google.protobuf:protobuf-java")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks {
    val examples = listOf(
        "Publisher",
        "Subscriber",
    )
    examples.forEach { example ->
        register(example, JavaExec::class) {
            description = "Run the $example example"
            mainClass.set("org.eclipse.uprotocol.zenoh.${example}")
            classpath(sourceSets["main"].runtimeClasspath)
        }
    }
}
