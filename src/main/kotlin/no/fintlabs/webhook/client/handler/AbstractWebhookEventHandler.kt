package no.fintlabs.webhook.client.handler

import no.fintlabs.webhook.client.config.WebhookClientProperties

abstract class AbstractWebhookEventHandler<T>(
    webhookClientProperties: WebhookClientProperties
): WebhookEventHandler<T> {

    abstract override val eventName: String

    override val callbacks: MutableCollection<String> by lazy {
        webhookClientProperties.getCallback(eventName)
    }

}