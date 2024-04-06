package ch.frankel.blog.micrometer

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient


@SpringBootApplication
class MicrometerTracingApplication {

    @Bean
    fun restClient(builder: RestClient.Builder) = builder.baseUrl("http://localhost:8080/done").build()
}

@RestController
class MicrometerController(private val restClient: RestClient, private val registry: ObservationRegistry) {

    private val logger = LoggerFactory.getLogger(MicrometerController::class.java)

    @GetMapping("/{message}")
    fun entry(@PathVariable message: String, @RequestHeader("X-done") done: String?) {
        logger.info("entry: $message")
        val observation = Observation.start("entry", registry)
        if (done == null) intermediate(observation)
        observation.stop()
    }

    fun intermediate(parent: Observation) {
        logger.info("intermediate")
        val observation = Observation.createNotStarted("intermediate", registry)
            .parentObservation(parent)
            .start()
        restClient.get()
            .header("X-done", "true")
            .retrieve()
            .toBodilessEntity()
        observation.stop()
    }
}

fun main(args: Array<String>) {
    runApplication<MicrometerTracingApplication>(*args)
}
