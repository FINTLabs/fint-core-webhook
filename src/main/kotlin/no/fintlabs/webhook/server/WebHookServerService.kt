package no.fintlabs.webhook.server

import no.fintlabs.webhook.server.annotation.WebHookServer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
@ConditionalOnBean(annotation = [WebHookServer::class])
class WebHookServerService(private val webHookCache: WebHookCache) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val webClient: WebClient = WebClient.create()

    fun callback(any: Any) {
        webHookCache.callbacks.forEach { callback ->
            webClient.post()
                .uri("$callback/event")
                .bodyValue(any)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                    {
                        logger.debug("Successuly sent payload to: $callback")
                    },
                    {
                        logger.warn("Failed to send payload, detaching callback: $callback")
                        webHookCache.removeCallback(callback)
                    }
                )
        }
    }

}