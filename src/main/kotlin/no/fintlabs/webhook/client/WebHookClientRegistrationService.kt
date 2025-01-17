package no.fintlabs.webhook.client

import jakarta.annotation.PostConstruct
import no.fintlabs.webhook.client.annotation.WebHookClient
import no.fintlabs.webhook.client.config.WebhookClientProperties
import no.fintlabs.webhook.client.model.ClientRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@ConditionalOnBean(annotation = [WebHookClient::class])
class WebHookClientRegistrationService(
    @Qualifier("webHookClient") private val webClient: WebClient,
    private val properties: WebhookClientProperties
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun register() {
        val registerUrl = "${properties.server}/webhook/register"
        val callbackUrl = "${properties.callback}/webhook"
        logger.debug("Attempting to register webhook at: $registerUrl")
        logger.debug("Callback url: $callbackUrl")

        webClient.post()
            .uri(registerUrl)
            .bodyValue(ClientRequest(callbackUrl))
            .exchangeToMono { Mono.just(it.statusCode()) }
            .subscribe(
                {
                    when (it) {
                        HttpStatus.OK -> logger.info("Webhook registered successfully!")
                        else -> logger.warn("Webhook failed to register")
                    }
                },
                {
                    logger.warn("Webhook failed to register: Server is likely down")
                }
            )
    }

}