package no.fintlabs.webhook.client

import no.fintlabs.webhook.client.annotation.WebhookClient
import no.fintlabs.webhook.client.config.WebhookClientProperties
import no.fintlabs.webhook.model.ClientRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@ConditionalOnBean(annotation = [WebhookClient::class])
class WebhookClientRegistrationService(
    properties: WebhookClientProperties,
    @Qualifier("webhookClient")
    private val webhookClient: WebClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val registerUrl = "${properties.server}/webhook/register"
    private val unregisterUrl = "${properties.server}/webhook/unregister"

    fun unregister(eventName: String, callbacks: Collection<String>, url: String = unregisterUrl) =
        webhookClient.post()
            .uri(url)
            .bodyValue(mapOf(eventName to callbacks))
            .exchangeToMono { Mono.just(it.statusCode()) }
            .subscribe(
                { logger.debug("Webhook unregistered successfully!") },
                { error -> logger.warn("Webhook unregistering failed: Server is likely down. Error: ${error.message}") }
            )

    fun register(eventName: String, callbacks: Collection<String>, url: String = registerUrl) =
        webhookClient.post()
            .uri(url)
            .bodyValue(ClientRequest(mapOf(eventName to callbacks.toSet())))
            .exchangeToMono { Mono.just(it.statusCode()) }
            .subscribe(
                { logger.debug("Webhook registered event: $eventName successfully!") },
                { error -> logger.warn("Webhook registration failed: Server is likely down. Error: ${error.message}") }
            )

}
