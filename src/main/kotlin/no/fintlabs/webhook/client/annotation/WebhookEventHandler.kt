package no.fintlabs.webhook.client.annotation

interface WebhookEventHandler<T> {
    /** The class of the event payload so we can deserialize JSON. */
    val eventType: Class<T>

    /** The url-s that will be called back to when an event is triggered. */
    val callbacks: MutableCollection<String>

    /** Process the deserialized event. */
    fun handleEvent(event: T)

}
