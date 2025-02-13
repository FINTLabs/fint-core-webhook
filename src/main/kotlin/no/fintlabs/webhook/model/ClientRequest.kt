package no.fintlabs.webhook.model

data class ClientRequest(
    val callbacks: Map<String, Set<String>>
)
