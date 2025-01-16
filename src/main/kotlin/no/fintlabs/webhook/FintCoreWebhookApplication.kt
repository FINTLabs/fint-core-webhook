package no.fintlabs.webhook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FintCoreWebhookApplication

fun main(args: Array<String>) {
	runApplication<FintCoreWebhookApplication>(*args)
}
