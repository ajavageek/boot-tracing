package ch.frankel.blog.agenttwo

import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@SpringBootApplication
class Agent2xApplication

@RestController
class Agent2xController {

    private val logger = LoggerFactory.getLogger(Agent2xController::class.java)

    @GetMapping("/{message}")
    @WithSpan
    fun entry(@PathVariable message: String, @RequestHeader("X-done") done: String?) {
        logger.info("entry: $message")
        if (done == null) intermediate()
    }

    @WithSpan
    fun intermediate() {
        logger.info("intermediate")
        RestClient.builder()
            .baseUrl("http://localhost:8080/done")
            .build()
            .get()
            .header("X-done", "true")
            .retrieve()
            .toBodilessEntity()
    }
}

fun main(args: Array<String>) {
    runApplication<Agent2xApplication>(*args)
}
