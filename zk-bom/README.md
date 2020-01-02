zk-bom [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.zkoss.zk/zk-bom/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.zkoss.zk/zk-bom)
======

A BOM (Bill of Materials) POM for ZK dependencies.

How to use
----------

### Maven

Add these lines in your `pom.xml`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.zkoss.zk</groupId>
            <artifactId>zk-bom</artifactId>
            <version>x.y.z</version><!-- replace with the actual version -->
            <type>pom</type>
            <scope>import</scope>
        </dependency>   
    </dependencies>
</dependencyManagement>
```

### Gradle >= 5.0

Add these lines in your `build.gradle`:

```groovy
dependencies {
    // replace with the actual version
    implementation(platform('org.zkoss.zk:zk-bom:x.y.z'))
}
```
