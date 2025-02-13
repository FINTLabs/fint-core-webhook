package no.fintlabs.webhook.client

import jakarta.annotation.PostConstruct
import no.fintlabs.webhook.client.annotation.WebhookClient
import no.fintlabs.webhook.client.config.WebhookClientProperties
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
    private val webhookClient: WebClient,
    private val handlerRegistry: WebhookHandlerRegistry
) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val registerUrl = "${properties.server}/webhook/register"
    private val unregisterUrl = "${properties.server}/webhook/unregister"

    fun unregister(clazzName: String, callbacks: Collection<String>, url: String = unregisterUrl) =
        webhookClient.post()
            .uri(url)
            .bodyValue(handlerRegistry.getCallbacks())
            .exchangeToMono { Mono.just(it.statusCode()) }
            .subscribe(
                {
                    logger.info("Webhook unregistered successfully!")
                },
                { error ->
                    logger.warn("Webhook unregistering failed: Server is likely down. Error: ${error.message}")
                }
            )

    fun register(clazzName: String, callbacks: Collection<String>, url: String = registerUrl) =
        webhookClient.post()
            .uri(unregisterUrl)
            .bodyValue(handlerRegistry.getCallbacks())
            .exchangeToMono { Mono.just(it.statusCode()) }
            .subscribe(
                {
                    logger.info("Webhook registered successfully!")
                },
                { error ->
                    logger.warn("Webhook registration failed: Server is likely down. Error: ${error.message}")
                }
            )

}
