# Zephyr Test
Testing project for Stateless and Client MVVM

## Prerequisites
* JDK Version: 1.8

## How to run
### Run Web Application
Run with `./gradlew appRun`

### Run Web Application in Debug Mode (Intellij)
1. Create a [Gradle] Task in [Run/Debug Configurations]
2. Type "appRunDebug" in [Run]
3. Select "zephyr-test" in [Gradle Project]
4. Click [OK] and [Run]
5. You can see "Listening for transport dt_socket at address: 5005" [Attach debugger] in console
6. Click [Attach debugger] -> Start to debug
7. Or create a [Remote JVM Debug] in [Run/Debug Configurations] and point to port 5050 
   
   -> [Run] and start to debug

### Run All tests
Run with `./gradlew test`

## How to Write a test
To write an automatic test with webdriver, extends `org.zkoss.zephyr.webdriver.WebDriverTestCase`