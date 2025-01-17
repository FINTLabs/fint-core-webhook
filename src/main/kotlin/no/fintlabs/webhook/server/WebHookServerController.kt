package no.fintlabs.webhook.server

import no.fintlabs.webhook.client.model.ClientRequest
import no.fintlabs.webhook.server.annotation.WebHookServer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhook")
@ConditionalOnBean(annotation = [WebHookServer::class])
class WebHookServerController(private val webHookCache: WebHookCache) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/register")
    fun register(@RequestBody clientRequest: ClientRequest) {
        logger.debug("Registering callback: ${clientRequest.callback}")
        webHookCache.addCallback(clientRequest.callback)
        logger.debug("Callbacks: {}", webHookCache.callbacks)
    }

    @PostMapping("/health")
    fun health(@RequestBody clientRequest: ClientRequest): ResponseEntity<Void> =
        if (webHookCache.callbackExists(clientRequest.callback)) {
            logger.debug("Health check passed for callback: ${clientRequest.callback}")
            ResponseEntity.ok().build()
        } else {
            logger.debug("Health check failed for callback: ${clientRequest.callback}")
            ResponseEntity.notFound().build()
        }

}