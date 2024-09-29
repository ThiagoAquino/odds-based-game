package odds

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BasedApplication

fun main(args: Array<String>) {
    runApplication<BasedApplication>(*args)
}
