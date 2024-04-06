package ch.frankel.blog.agentone

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@SpringBootApplication
class Agent1xApplication

@RestController
class Agent1xController {

    private val logger = LoggerFactory.getLogger(Agent1xController::class.java)

    @GetMapping("/{message}")
    fun entry(@PathVariable message: String, @RequestHeader("X-done") done: String?) {
        logger.info("entry: $message")
        if (done == null) intermediate()
    }

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
    runApplication<Agent1xApplication>(*args)
}
