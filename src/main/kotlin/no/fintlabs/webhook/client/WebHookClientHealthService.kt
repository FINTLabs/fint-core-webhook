package no.fintlabs.webhook.client

import no.fintlabs.webhook.client.annotation.WebHookClient
import no.fintlabs.webhook.client.config.WebhookClientProperties
import no.fintlabs.webhook.client.model.ClientRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@ConditionalOnBean(annotation = [WebHookClient::class])
class WebhookClientHealthChecker(
    private val properties: WebhookClientProperties,
    @Qualifier("webHookClient") private val webClient: WebClient,
    private val webHookClientRegistration: WebHookClientRegistrationService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(initialDelay = 10000L, fixedDelay = 30000L)
    private fun healthCheck() {
        val healthCheckUrl = "${properties.server}/webhook/health"
        logger.debug("Performing health check at: $healthCheckUrl")

        webClient.post()
            .uri(healthCheckUrl)
            .bodyValue(ClientRequest("${properties.callback}/webhook"))
            .exchangeToMono { Mono.just(it.statusCode()) }
            .subscribe(
                {
                    when (it) {
                        HttpStatus.OK -> logger.debug("Health check successful")
                        else -> {
                            logger.warn("Health check failed: $it")
                            webHookClientRegistration.register()
                        }
                    }
                },
                {
                    logger.error("Health check encountered an error: The server is most likely down")
                    webHookClientRegistration.register()
                }
            )
    }

}