package net.jupw.hubertus.app

import net.jupw.hubertus.business.services.BookingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class BusinessConfiguration {

    @Bean
    @Scope("singleton")
    fun bookingService(conf: net.jupw.hubertus.business.Configuration) = BookingService(conf)

}