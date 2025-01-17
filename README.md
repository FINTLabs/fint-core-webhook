# FINT Core Webhook

This project provides a simple approach to set up **webhook client** and **webhook server** functionality within a Spring Boot application. The main purpose is to enable a service (the client) to register callbacks with a remote server, perform health checks, and receive events via configured callback URLs.

## Overview

The project is split into two parts:

1. **Webhook Client**
    - Handles registration of the client to the server
    - Subscribes to callbacks from the server
    - Implements periodic health checks

2. **Webhook Server**
    - Maintains a list of client callbacks
    - Provides an endpoint for clients to register
    - Performs health checks to ensure client callbacks are valid
    - Sends events back to client callbacks

---

## Webhook Client usage
1. Annotate your Application with @WebHookClient
2. Create a bean of WebHookClientService with the specified type that should be processed.

```kotlin
    @Bean
    fun webHookClientService(objectMapper: ObjectMapper): WebhookClientService<Test> {
        return WebhookClientService(Test::class.java, this::test, objectMapper)
    }

    fun test(test: Test) {
        // handle test
    }
```
}
## Webhook Server usage
1. Annotate your Application with @WebHookServer
2. Dependency Inject WebHookServerService and call callback

```kotlin

    fun test() {
        webHookServerService.callback(Testt("OK", "BINGO"))
    }
```