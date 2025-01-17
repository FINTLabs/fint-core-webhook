package no.fintlabs.webhook.client.config

import no.fintlabs.webhook.client.annotation.WebHookClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "fint.webhook.client")
@ConditionalOnBean(annotation = [WebHookClient::class])
data class WebhookClientProperties(
    var server: String = "",
    var callback: String = ""
)