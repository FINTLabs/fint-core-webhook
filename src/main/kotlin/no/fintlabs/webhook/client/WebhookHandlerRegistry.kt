package no.fintlabs.webhook.client

import no.fintlabs.webhook.client.annotation.WebhookClient
import no.fintlabs.webhook.client.annotation.WebhookEventHandler
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(annotation = [WebhookClient::class])
class WebhookHandlerRegistry(
    private val webHookClientRegistrationService: WebhookClientRegistrationService,
    handlers: List<WebhookEventHandler<*>>
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val handlerMap: MutableMap<String, WebhookEventHandler<*>> =
        handlers.associateBy { it.eventType.name }.toMutableMap()

    fun getCallbacks(): Map<String, Set<String>> =
        handlerMap.mapValues { (_, handler) -> handler.callbacks.toSet() }

    fun removeHandler(eventName: String) =
        handlerMap.remove(eventName)
            ?.callbacks
            ?.toSet()
            ?.also { webHookClientRegistrationService.unregister(eventName, it) }

    fun removeCallbacks(eventName: String, callbacks: Collection<String>) =
        handlerMap[eventName]?.let { handler ->
            val callbacksSet = callbacks.toSet()
            handler.callbacks.removeAll(callbacksSet)
            webHookClientRegistrationService.unregister(eventName, callbacksSet)
        }

    fun addCallbacks(eventName: String, callbacks: Collection<String>) =
        handlerMap[eventName]?.let { handler ->
            handler.callbacks.addAll(callbacks)
            webHookClientRegistrationService.register(eventName, callbacks)
        } ?: run { logger.info("No handler found for event: $eventName") }

    fun getAllHandlers(): Collection<WebhookEventHandler<*>> = handlerMap.values

    fun getHandler(eventName: String): WebhookEventHandler<*>? = handlerMap[eventName]
}
