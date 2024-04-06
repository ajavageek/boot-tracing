package ch.frankel.blog.micrometer

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
class MicrometerController(private val restClient: RestClient) {

    private val logger = LoggerFactory.getLogger(MicrometerController::class.java)

    @GetMapping("/{message}")
    fun entry(@PathVariable message: String, @RequestHeader("X-done") done: String?) {
        logger.info("entry: $message")
        if (done == null) intermediate()
    }

    fun intermediate() {
        logger.info("intermediate")
        restClient.get()
                  .header("X-done", "true")
                  .retrieve()
                  .toBodilessEntity()
    }
}

fun main(args: Array<String>) {
    runApplication<MicrometerTracingApplication>(*args)
}
