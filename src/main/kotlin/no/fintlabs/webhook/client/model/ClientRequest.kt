package no.fintlabs.webhook.client.model

data class ClientRequest(
    val callbacks: Map<String, Set<String>>
)
