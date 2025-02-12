package no.fintlabs.webhook.client

import no.fintlabs.webhook.client.annotation.WebhookClient
import no.fintlabs.webhook.client.annotation.WebhookEventHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(annotation = [WebhookClient::class])
class WebhookHandlerRegistry(
    private val webHookClientRegistrationService: WebhookClientRegistrationService,
    handlers: List<WebhookEventHandler<*>>
) {

    private val handlerMap: MutableMap<String, WebhookEventHandler<*>> =
        handlers.associateBy { it.eventType.name }.toMutableMap()

    fun getCallbacks(): Map<String, Set<String>> =
        handlerMap.mapValues { (_, handler) -> handler.callbacks.toSet() }

    fun removeHandler(clazzName: String) =
        handlerMap.remove(clazzName)
            ?.callbacks
            ?.toSet()
            ?.also { webHookClientRegistrationService.unregister(clazzName, it) }

    fun removeCallbacks(clazzName: String, callbacks: Collection<String>) =
        handlerMap[clazzName]?.let { handler ->
            val callbacksSet = callbacks.toSet()
            handler.callbacks.removeAll(callbacksSet)
            webHookClientRegistrationService.unregister(clazzName, callbacksSet)
        }

    fun addCallbacks(clazzName: String, callbacks: Collection<String>) =
        handlerMap[clazzName]?.let { handler ->
            handler.callbacks.addAll(callbacks)
            webHookClientRegistrationService.register(clazzName, callbacks)
        }

    fun removeHandler(clazz: Class<*>) = removeHandler(clazz.name)

    fun getAllHandlers(): Collection<WebhookEventHandler<*>> = handlerMap.values

    fun getHandler(clazz: Class<*>): WebhookEventHandler<*>? = handlerMap[clazz.name]

    fun getHandler(clazzName: String): WebhookEventHandler<*>? = handlerMap[clazzName]

    fun addCallbacks(clazz: Class<*>, callbacks: Collection<String>) = addCallbacks(clazz.name, callbacks)
    fun removeCallbacks(clazz: Class<*>, callbacks: Collection<String>) = removeCallbacks(clazz.name, callbacks)
    fun removeCallback(clazzName: String, callback: String) = removeCallbacks(clazzName, setOf(callback))
    fun removeCallback(clazz: Class<*>, callback: String) = removeCallback(clazz.name, callback)
    fun addCallback(clazzName: String, callback: String) = addCallbacks(clazzName, setOf(callback))
    fun addCallback(clazz: Class<*>, callback: String) = addCallback(clazz.name, callback)
}
