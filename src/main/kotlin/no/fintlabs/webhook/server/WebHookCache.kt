package no.fintlabs.webhook.server

import no.fintlabs.webhook.server.annotation.WebHookServer
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(annotation = [WebHookServer::class])
class WebHookCache {

    val callbacks: MutableSet<String> = mutableSetOf()

    fun addCallback(callback: String) = callbacks.add(callback)

    fun callbackExists(callback: String) = callbacks.contains(callback)

    fun removeCallback(callback: String) = callbacks.remove(callback)

}