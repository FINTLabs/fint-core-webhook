package no.fintlabs.webhook.server.annotation

/**
 * By annotating a class with @WebhookServer, we import the required
 * configuration to set up the serer side of fint-core-webhook.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebhookServer()
