package no.fintlabs.webhook.client.config

import no.fintlabs.webhook.client.annotation.WebHookClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@ConditionalOnBean(annotation = [WebHookClient::class])
class WebClientConfig {

    @Bean
    fun webHookClient() = WebClient.create()

}