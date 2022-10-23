## How to Debug Gradle Annotation Processor
1. Run the following command
```$ ./gradlew --no-daemon -Dorg.gradle.debug=true :zephyr:clean :zephyr:compileJava```
2. Create a remote debug configuration with default parameters: **Run -> Edit Configurations... -> Add New Configuration (Alt + Insert) -> Remote JVM Debug** (in Intellij).
3. Set a break point in your source code.
4. Run the remote debug in Intellij.

More details, please refer to https://stackoverflow.com/a/52186665