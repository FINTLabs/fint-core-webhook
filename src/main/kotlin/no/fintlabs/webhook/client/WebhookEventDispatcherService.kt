package no.fintlabs.webhook.client

import com.fasterxml.jackson.databind.ObjectMapper
import no.fintlabs.webhook.client.annotation.WebhookClient
import no.fintlabs.webhook.client.handler.WebhookEventHandler
import no.fintlabs.webhook.client.handler.WebhookHandlerRegistry
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(annotation = [WebhookClient::class])
class WebhookEventDispatcherService(
    private val handlerRegistry: WebhookHandlerRegistry,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun dispatchEvent(eventName: String, payload: Any): Boolean =
        handlerRegistry.getHandler(eventName)?.let { handler ->
            try {
                val event = objectMapper.convertValue(payload, handler.eventType)
                @Suppress("UNCHECKED_CAST")
                (handler as WebhookEventHandler<Any>).handleEvent(event!!)
                true
            } catch (ex: Exception) {
                logger.error("Error processing event for event type: $eventName", ex)
                false
            }
        } ?: run {
            logger.warn("No handler found for event type: $eventName")
            false
        }

}
