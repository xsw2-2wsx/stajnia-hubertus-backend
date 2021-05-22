package net.jupw.hubertus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HubertusBookingApplication

fun main(args: Array<String>) {
	runApplication<HubertusBookingApplication>(*args)
}
