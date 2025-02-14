package no.fintlabs.webhook.server

import no.fintlabs.webhook.server.annotation.WebhookServer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
@ConditionalOnBean(annotation = [WebhookServer::class])
class WebhookServerService(
    private val webHookCache: WebhookCache
) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val webClient: WebClient = WebClient.create()

    fun callback(eventName: String, payload: Any) =
        webHookCache.getCallbacks(eventName).forEach { callback ->
            webClient.post()
                .uri("$callback/webhook/event/$eventName")
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                    {
                        logger.info("Successuly sent payload to: $callback")
                    },
                    { error ->
                        logger.warn("Failed to send payload, detaching callback: $callback with error: ${error.message}")
                        webHookCache.removeCallback(eventName, callback)
                    }
                )
        }

}