package no.fintlabs.webhook.client.annotation

/**
 * By annotating a class with @WebhookClient, we import the required
 * configuration to set up the client side of fint-core-webhook.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class WebhookClient()
