package no.fintlabs.webhook.client

import com.fasterxml.jackson.databind.ObjectMapper
import no.fintlabs.webhook.client.annotation.WebhookEventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WebhookEventDispatcherService(
    private val handlerRegistry: WebhookHandlerRegistry,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Dispatches the event based on its type.
     *
     * @param eventName the unique identifier for the event type.
     * @param payload the JSON payload as a String.
     * @return true if the event was successfully processed, false otherwise.
     */
    fun dispatchEvent(eventName: String, payload: String): Boolean =
        handlerRegistry.getHandler(eventName)?.let { handler ->
            try {
                val event = objectMapper.readValue(payload, handler.eventType)
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
