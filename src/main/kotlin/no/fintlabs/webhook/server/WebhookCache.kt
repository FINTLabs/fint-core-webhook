package no.fintlabs.webhook.server

import no.fintlabs.webhook.server.annotation.WebhookServer
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
@ConditionalOnBean(annotation = [WebhookServer::class])
class WebhookCache {

    val cache: ConcurrentHashMap<String, MutableSet<String>> = ConcurrentHashMap()

    fun addCallbacks(callbacks: Map<String, Collection<String>>) =
        callbacks.forEach {
            cache.computeIfAbsent(it.key) { mutableSetOf() }
                .addAll(it.value)
        }

    fun getCallbacks(type: String) =
        cache.getOrDefault(type, mutableSetOf())

    fun callbacksExists(callbacks: Map<String, Collection<String>>) =
        callbacks.all { cache.containsKey(it.key) && cache.getOrDefault(it.key, mutableSetOf()).containsAll(it.value) }

    fun removeCallback(eventName: String, callback: String) =
        cache.getOrDefault(eventName, mutableSetOf()).remove(callback)

}