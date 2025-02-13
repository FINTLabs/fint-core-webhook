package no.fintlabs.webhook.server

import no.fintlabs.webhook.model.ClientRequest
import no.fintlabs.webhook.server.annotation.WebhookServer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
@ConditionalOnBean(annotation = [WebhookServer::class])
class WebhookServerController(
    private val webHookCache: WebhookCache
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/register")
    fun register(@RequestBody clientRequest: ClientRequest) {
        logger.info("Registering callback: ${clientRequest.callbacks}")
        webHookCache.addCallbacks(clientRequest.callbacks)
        logger.info("Callbacks: {}", webHookCache.cache)
    }

    @PostMapping("/health")
    fun health(@RequestBody clientRequest: ClientRequest): ResponseEntity<Void> =
        if (webHookCache.callbacksExists(clientRequest.callbacks)) {
            logger.info("Health check passed for callback: ${clientRequest.callbacks}")
            ResponseEntity.ok().build()
        } else {
            logger.info("Health check failed for callbacks: ${clientRequest.callbacks}")
            ResponseEntity.notFound().build()
        }

}
