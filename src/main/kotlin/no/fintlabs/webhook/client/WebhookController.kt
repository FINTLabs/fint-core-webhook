package no.fintlabs.webhook.client

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhook")
class WebhookController(
    private val dispatcherService: WebhookEventDispatcherService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/event/{eventName}")
    fun event(@PathVariable eventName: String, @RequestBody payload: String): ResponseEntity<Unit> {
        logger.info("Received event of type: $eventName")

        val success = dispatcherService.dispatchEvent(eventName, payload)
        return if (success) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

}
