package no.fintlabs.webhook.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory

class WebhookClientService<T>(
    private val clazz: Class<T>,
    private val function: (T) -> Unit,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun handleEvent(payload: String) {
        try {
            val readValue = objectMapper.readValue(payload, clazz)
            function(readValue)
        } catch (e: Exception) {
            logger.error("Failed to handle event: $e")
        }
    }

}