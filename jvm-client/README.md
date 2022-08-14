# JVM Client

You want to make calls to the Make API from with your Java or Kotlin code base.

JVM Client is the right choice for you.
* Shields you from complexities with setting up your own HTTP client
* Provides reliability through configurable retry and timeout policies
* Provides a friendly and easy-to-understand interface.

The client comes in two flavours - [Kotlin](#the-kotlin-client) and [Java](#the-java-client).

## The Kotlin client

* Performant asynchronous model based on [coroutines](https://kotlinlang.org/docs/coroutines-guide.html).
* Graceful error handling thanks to the [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result) return type. Use `onSuccess` and `onFailure` instead of catching exceptions.
* Strongly typed interface. You can't simply misplace parameter values. The compiler will stop you.
* Suitable for Android development or simply any Kotlin project.

### [Check it out!](docs/kotlin-client.md)

## The Java client
* Asynchronous model based on tried and tested `CompletableFuture`.
* Graceful error handling and compatibility with Java 8.
* Simple to use interface with lots of flexibility. Only use what you need.
* Suitable for any enterprise development based on Java.

### [Check it out!](docs/java-client.md)
