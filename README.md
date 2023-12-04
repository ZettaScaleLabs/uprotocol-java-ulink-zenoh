# uprotocol-ulink-zenoh-java

Java uLink Library for zenoh transport

# Installation

* Install zenoh-java

```shell
# You need to install Rust, Kotlin, Gradle, Android SDK beforehead
# Get the code
git clone https://github.com/ZettaScaleLabs/zenoh-kotlin.git -b java_compat
cd zenoh-kotlin
# Build and install
gradle build
gradle publishToMavenLocal
```

* Install uprotocol-java

```shell
# Get the code
git clone https://github.com/eclipse-uprotocol/uprotocol-java.git -b ea94d1b
cd uprotocol-java
# Build and publish to Maven Local repository
mvn compile
mvn install
```

* Build uprotocol-java-ulink-zenoh

```shell
gradle build
```

# Test

```shell
gradle test -i
```
