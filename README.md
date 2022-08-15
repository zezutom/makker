# Makker

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/zezutom/makker/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/zezutom/makker/tree/main)
[![codecov](https://codecov.io/gh/zezutom/makker/branch/main/graph/badge.svg?token=8GT2U3IDWQ)](https://codecov.io/gh/zezutom/makker)

Make (formerly Integromat) connector suite. Choose how do you want to manage your low-code and no-code scenarios from your code!

Makker comprises different modules. Pick the one that suits your needs.

## Make Adapter
Do you want to build your own server or client connected to Make, but don't want to start from scratch?

Make Adapter is a low-level Make connector.
* Matches the Make API almost 1:1.
* Provides a common logic reusable across your (JVM) client and server implementations.

[Check it out](./common)

## Server
You have a web client, but it cannot connect directly to the Make API
due to various restrictions, such as CORS policy.

Or, perhaps you just want to quickly spin up an API gateway connected
to your Make account.

Either way, Server is the right choice. You can:
* Configure the server to connect to your Make account. Perfect choice when you deal with Make within your own organisation.
* Expose the server to the world and let the client pass Make authentication token with each request. Great option if you just want to share the API, but keep access to your Make project private.
* Configure CORS and other restrictive policies to allow for access from any domain of your choice.

[Check it out](./server)

## JVM Client

You want to make calls to the Make API from with your Java or Kotlin code base.

JVM Client is the right choice for you.
* Shields you from complexities with setting up your own HTTP client
* Provides reliability through configurable retry and timeout policies
* Provides a friendly and easy-to-understand interface.

[Check it out](./jvm-client)

## JS Client

So, you want to manage your Make project from the browser?

JS Client, in tandem with [Server](#server), is the right choice for you.
Once you spin up the server you can:

* Import the JS library in your project and easily instantiate the client.
* The client benefits from extra features provided by the [Server](#server) instance acting as a proxy. For example, you can choose to send complex JSON objects (blueprints) in base64-encoded format.
* Similarly to [JVM client](#jvm-client) there are guarantees around retries and timeouts.
* The client provides a friendly and easy-to-understand interface.
* Asynchronous and performant. Promises instead of callbacks.

[Check it out](./js-client)


