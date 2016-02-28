# JCLRE
JCLRE (stands for Java CLass REloading) is a proof-of-concept which makes code reloading possible even on a regular HotSpot JVM.

HotSwap thing which existed in JDK for ages doesn't allow you to add fields, methods to your classes or modify methods' signatures. It limits allowed modifications only by changing methods' bodies.

# Usage
You compile it the usual way:
```
$ mvn clean install
```
And then you use the compiled jar as a javaagent:
```
$ java -javaagent:core-0.1-SNAPSHOT.jar -classpath '...' org.example.Something
```

## Demo
### Regular JVM HotSwap
![Regular JVM HotSwap](https://raw.githubusercontent.com/msavelyev/jclre/master/doc/jvm-hotswap.gif)

### Jclre in action
![Jclre in action](https://raw.githubusercontent.com/msavelyev/jclre/master/doc/jclre.gif)
