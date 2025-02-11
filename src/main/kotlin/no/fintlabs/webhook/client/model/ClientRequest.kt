package no.fintlabs.webhook.client.model

data class ClientRequest(
    val clazz: Class<*>,
    val callback: String
)
