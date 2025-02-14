package no.fintlabs.webhook.client

import no.fintlabs.webhook.client.annotation.WebhookClient
import no.fintlabs.webhook.client.config.WebhookClientProperties
import no.fintlabs.webhook.model.ClientRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@ConditionalOnBean(annotation = [WebhookClient::class])
class WebhookClientHealthChecker(
    properties: WebhookClientProperties,
    @Qualifier("webhookClient") private val webhookClient: WebClient,
    private val handlerRegistry: WebhookHandlerRegistry,
    private val webHookClientRegistration: WebhookClientRegistrationService
) {

    private val healthUrl = "${properties.server}/webhook/health"
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(initialDelay = 1000L, fixedDelay = 30000L)
    private fun healthCheck() =
        handlerRegistry.getCallbacks().let { callbacks ->
            callbacks.forEach { callback ->
                logger.debug("Performing health check at: $healthUrl with callbacks: $callbacks")
                webhookClient.post()
                    .uri(healthUrl)
                    .bodyValue(ClientRequest(mapOf(callback.key to callback.value)))
                    .exchangeToMono { Mono.just(it.statusCode()) }
                    .subscribe(
                        { status ->
                            if (status.is2xxSuccessful)
                                logger.debug("Health check was successful!")
                            else handleHealthCheckFailiure(callback.key, callback.value,  "${status.value()}")
                        },
                        { handleHealthCheckFailiure(callback.key, callback.value, it.message) }
                    )
            }
        }

    fun handleHealthCheckFailiure(eventName: String, callbacks: Collection<String>, errorMessage: String?) =
        logger.error("Health check encountered an error: $errorMessage")
            .also { webHookClientRegistration.register(eventName, callbacks) }
}
