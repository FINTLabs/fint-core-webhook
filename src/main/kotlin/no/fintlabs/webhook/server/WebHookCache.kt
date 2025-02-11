package no.fintlabs.webhook.server

import no.fintlabs.webhook.server.annotation.WebHookServer
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
@ConditionalOnBean(annotation = [WebHookServer::class])
class WebHookCache {

    val callbacks: ConcurrentHashMap<String, MutableSet<String>> = ConcurrentHashMap()

    fun addCallback(clazz: Class<*>, callback: String) =
        callbacks.computeIfAbsent(clazz.name) { _: String -> mutableSetOf() }
            .add(callback)

    fun getCallbacks(clazz: Class<*>) =
        callbacks.getOrDefault(clazz.name, mutableSetOf())

    fun callbackExists(clazz: Class<*>, callback: String) =
        callbacks.getOrDefault(clazz.name, mutableSetOf())
            .contains(callback)

}