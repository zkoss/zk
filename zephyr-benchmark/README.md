# Zephyr Benchmark

-----

## How to run benchmarks
* Runs with Arguments
```shell
# Ex: 1 run (fork), 8 warmup iterations, 10 measurement iterations
./gradlew :jmh --args="-f 1 -wi 8 -i 10"
```
* Runs with Verify (i.e. `-f 1 -wi 1 -i 1`)
```shell
./gradlew :jmh -Pverify="true"
```
* Runs with JMH Task Help
```shell
./gradlew :jmhHelp
```