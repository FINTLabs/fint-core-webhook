package no.fintlabs.webhook.client

import no.fintlabs.webhook.client.annotation.WebhookClient
import no.fintlabs.webhook.client.config.WebhookClientProperties
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
    @Qualifier("webHookClient") private val webClient: WebClient,
    private val handlerRegistry: WebhookHandlerRegistry,
    private val webHookClientRegistration: WebhookClientRegistrationService
) {

    private val healthUrl = "${properties.server}/webhook/health"
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(initialDelay = 10000L, fixedDelay = 30000L)
    private fun healthCheck() =
        handlerRegistry.getCallbacks().let { callbacks ->
            callbacks.forEach { callback ->
                logger.info("Performing health check at: $healthUrl with callbacks: $callbacks")

                webClient.post()
                    .uri(healthUrl)
                    .bodyValue(callbacks)
                    .exchangeToMono { Mono.just(it.statusCode()) }
                    .subscribe(
                        { status ->
                            if (status.is2xxSuccessful) logger.info("Health check successful")
                            else {
                                logger.warn("Health check encountered an error with status code: $status")
                                webHookClientRegistration.register(callback.key, callback.value)
                            }
                        },
                        { error ->
                            logger.error("Health check encountered an error: ${error.message}")
                            webHookClientRegistration.register(callback.key, callback.value)
                        }
                    )
            }
        }
}
