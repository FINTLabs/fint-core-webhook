package no.fintlabs.webhook.client

import no.fintlabs.webhook.client.annotation.WebHookClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
@ConditionalOnBean(annotation = [WebHookClient::class])
class WebHookController(
    private val webHookClientService: WebhookClientService<*>
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/event")
    fun event(@RequestBody payload: String) {
        logger.debug("Received webhook event!")
        webHookClientService.handleEvent(payload)
    }

}