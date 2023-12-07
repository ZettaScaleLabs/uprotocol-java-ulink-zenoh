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
git clone https://github.com/eclipse-uprotocol/uprotocol-java.git -b uprotocol-java-1.5.4
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
# Run all the test
gradle cleanTest test -i
# Only run specific test
gradle cleanTest test --tests org.eclipse.uprotocol.ulink.zenoh.ULinkTest
```

# Run examples

```shell
# publish data
gradle :examples:Publisher
# subscribe data
gradle :examples:Subscriber
```
