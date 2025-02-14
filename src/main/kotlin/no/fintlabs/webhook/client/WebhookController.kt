package no.fintlabs.webhook.client

import no.fintlabs.webhook.client.annotation.WebhookClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhook")
@ConditionalOnBean(annotation = [WebhookClient::class])
class WebhookController(
    private val dispatcherService: WebhookEventDispatcherService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/event/{eventName}")
    fun event(@PathVariable eventName: String, @RequestBody payload: Any): ResponseEntity<Unit> {
        logger.debug("Received event of type: $eventName")

        val success = dispatcherService.dispatchEvent(eventName, payload)
        return if (success) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

}
