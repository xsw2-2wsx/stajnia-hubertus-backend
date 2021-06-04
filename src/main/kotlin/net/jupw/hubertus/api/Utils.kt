package net.jupw.hubertus.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap

fun <T> T?.toResponseEntity(
    status: HttpStatus = HttpStatus.OK,
    headers: MultiValueMap<String, String>? = null
): ResponseEntity<T> = ResponseEntity(this, headers, status)