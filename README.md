# [ZK](http://www.zkoss.org/) [![Build Status](https://github.com/zkoss/zk/workflows/zk-build/badge.svg)](https://github.com/zkoss/zk/actions?query=workflow%3Azk-build) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.zkoss.zk/zk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.zkoss.zk/zk) [![Code Climate](https://codeclimate.com/github/zkoss/zk/badges/gpa.svg)](https://codeclimate.com/github/zkoss/zk)
ZK is a highly productive Java framework for building amazing enterprise web
and mobile applications.

## Resources

### Documentation
* [ZK Demo](https://www.zkoss.org/zkdemo/)
* [Tutorial](https://www.zkoss.org/documentation#Getting_Started)
* [ZK Essentials](https://books.zkoss.org/zkessentials-book/master/)
* [ZK Developer's Reference](http://books.zkoss.org/wiki/ZK_Developer%27s_Reference)
* [Javadoc API](http://www.zkoss.org/javadoc/latest/zk/)
* [Documentation](https://www.zkoss.org/documentation)

### Binary Download

* [Download](http://www.zkoss.org/download/zk/ce)

### License

 * [LGPL](http://www.gnu.org/licenses/lgpl-2.1.html)

## [Product Overview](http://www.zkoss.org/product/zk)

### Simply Java

Since 2005, ZK has been known for its "Ajax without JavaScript" approach, allowing developers to build rich internet applications without needing knowledge of Ajax or JavaScript.

In ZK, the client engine acts as the pitcher, and the update engine as the catcher, making Ajax communication invisible to the developer. When users trigger events by interacting with UI, ZK automatically encapsulates and sends them to the appropriate event listeners.

User interfaces are rendered in the browser but represented on the server as Plain Old Java Objects (POJO) components. Any changes made to these server-side POJO components are automatically synced to the client-side interface.

By running code on the server, developers can fully utilize Java technologies such as Java EE and Spring, making it easy to access backend data and services.


### Server+client Fusion

ZK's Server+client Fusion gives developers the freedom to leverage the best
of both sides.

The server-centric solution to Ajax brings a productivity boost, robustness,
and security to Web application development; while client side solutions
endows Web applications with greater controllability and the ability to
leverage client side resources.

ZK marries the benefits of both to bring forth a developer-centric approach
where developers continue to build large scale enterprise applications with
all the robustness of Java technologies, but also are offered the flexibility
to work directly with ZK's jQuery based widgets to further enhance user
experience.

### Declarative Programming

ZK User Interface Markup Language (ZUML) makes the design of rich user interfaces
as simple as authoring HTML pages. ZUML is a variant of XUL inheriting all
features available to XML, and separates the UI definition from the run-time logic.

ZUML also allows developers to automate CRUD between UI components and the data
source with annotations, data binding and MVVM.


# Build Community Edition (CE)
## 1. Set a different version
* run `bin/upver.sh` to update version number in all pom.xml files.
* We recommend you to use a different version number from the official release version to distinguish it from the official release, e.g. add a suffix "-xyz".
## 2. Build CE jar only
* run `./gradlew clean build -x test -x tscheck -x jscheck -PcleanZKOnly=true -PbuildZKOnly=true`
* Need to run `gradlew` to ensure using the expected gradle version
* `-x tscheck -x jscheck` : skip these checking because they requires ZK EE modules
## 3. Install jar into your maven local repository
* run `./gradlew publishToMavenLocal`