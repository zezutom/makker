# Makker Server

This is your API gateway to Make.

## When to use it?
You have a web client, but it cannot connect directly to the Make API due to various restrictions, such as CORS policy.

Or, perhaps you just want to quickly spin up an API gateway connected to your Make account.

Either way, Server is the right choice. You can:

## How to use it?
Configure the server to connect to your Make account. Perfect choice when you deal with Make within your own organisation.

[Learn more](#connect-the-server-to-make)

OR

Expose the server to the world and let the client pass Make authentication token with each request. Great option if you just want to share the API, but keep access to your Make project private.
Configure CORS and other restrictive policies to allow for access from any domain of your choice.

[Learn more](#get-started-in-under-5-minutes)

## Get started in under 5 minutes

```shell
../gradlew buildFatJar
java -jar ./build/libs/makker-server.jar
```

Your server should now be available at http://localhost:8080.

The running instance is NOT connected to any Make project. You can start making client calls
passing the `X-Auth-Token` header as outlined in the [API spec](openapi.md).

## Connect the server to Make

```shell
../gradlew buildFatJar
java -jar ./build/libs/makker-server.jar -DMAKE_AUTH_TOKEN=your-make-api-token
```

The running instance is now connected to the Make project identified by the provided api token. The server connects to the EU region. 
You can start making client calls as outlined in the [API spec](openapi.md). You no longer need to pass the `X-Auth-Token` header.

If you need to change the region or make any other config adjustments, you can do so by modifying [application.conf](src/main/resources/application.conf).



