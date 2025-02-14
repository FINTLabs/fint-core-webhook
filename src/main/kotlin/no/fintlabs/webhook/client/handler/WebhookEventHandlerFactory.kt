package no.fintlabs.webhook.client.handler

import no.fintlabs.webhook.client.config.WebhookClientProperties

inline fun <reified T> createWebhookEventHandler(
    webhookClientProperties: WebhookClientProperties,
    eventName: String,
    noinline handleEvent: (T) -> Unit
): WebhookEventHandler<T> = object : AbstractWebhookEventHandler<T>(webhookClientProperties) {
    override val eventName: String = eventName
    override val eventType: Class<T> = T::class.java
    override fun handleEvent(event: T) = handleEvent(event)
}