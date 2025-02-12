package no.fintlabs.webhook.client.config

import no.fintlabs.webhook.client.annotation.WebhookClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "fint.webhook.client")
@ConditionalOnBean(annotation = [WebhookClient::class])
data class WebhookClientProperties(
    var server: String = "",
    var callbacks: Map<String, MutableList<String>> = mutableMapOf()
)