package net.jupw.hubertus.api

import net.jupw.hubertus.api.models.ApiError
import net.jupw.hubertus.api.models.ApiSubError
import net.jupw.hubertus.app.exceptions.UserNotFoundException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException

@Suppress("UNCHECKED_CAST", "UNUSED")
@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {

    companion object {
        private const val CONTACT_ADMIN_SUGGESTED_ACTION = "Proszę skontakuj się z administratorem systemu"
        private const val CONTACT_IF_ERROR_SUGGESTED_ACTION =
            "Jeśli uważasz że to błąd, skontaktuj się z administratorem systemu"
        private const val RECHECK_DATA_SUGGESTED_ACTION = "Sprawdź poprawność wprowadzonych danych"

        private const val UNKNOWN_ERROR_MESSAGE = "Przepraszamy, coś poszło nie tak"

        private val log: Logger = LogManager.getLogger()
    }

    @Autowired
    private lateinit var request: HttpServletRequest

    private fun error(conf: ApiError.() -> Unit): ResponseEntity<ApiError> {
        val error = ApiError().apply {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            message = UNKNOWN_ERROR_MESSAGE
            suggestedAction = CONTACT_ADMIN_SUGGESTED_ACTION
            subErrors = emptyList()
        }
        error.conf()
        return error.toResponseEntity(status = HttpStatus.valueOf(error.status))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException) =
        error {
            status = HttpStatus.NOT_FOUND.value()
            message = "Podany użytkownik nie istnieje"
            suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION
        }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<ApiError> =
        error {
            status = HttpStatus.UNAUTHORIZED.value()
            message = "Nieprawidłowa nazwa użytkownika lub hasło"
            suggestedAction = "Upewnij się, że CapsLock jest wyłączony i spróbuj ponownie"
        }

    @ExceptionHandler(DisabledException::class)
    fun handleAccountDisabled(ex: DisabledException): ResponseEntity<ApiError> =
        error {
            status = HttpStatus.FORBIDDEN.value()
            message = "Twoje konto zostało wyłączone"
        }

    @ExceptionHandler(LockedException::class)
    fun handleAccountLocked(ex: LockedException) =
        error {
            status = HttpStatus.FORBIDDEN.value()
            message = "Twoje konto zostało zablokowane"
        }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ApiError> {
        val subErrorCollection = ex.constraintViolations.map { ApiSubError().apply {
            message = "Nieprawidłowa wartość: ${it.invalidValue} - ${it.message}"
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION
        }}

        return error {
            status = HttpStatus.BAD_REQUEST.value()
            message = "Nieprawidłowe dane"
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION
            subErrors = subErrorCollection
        }
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val apiSubErrors: MutableList<ApiSubError> = LinkedList()

        ex.bindingResult.fieldErrors.forEach {
            apiSubErrors.add(
                ApiSubError().apply {  
                    message = "Nieprawidłowa wartość: ${it.rejectedValue} - ${it.defaultMessage}"
                    suggestedAction = RECHECK_DATA_SUGGESTED_ACTION
                }
            )
        }

        return error {
            status = HttpStatus.BAD_REQUEST.value()
            message = "Nieprawidłowe dane"
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION
            subErrors = apiSubErrors
        } as ResponseEntity<Any>
    }

    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        error {
            status = HttpStatus.BAD_REQUEST.value()
        } as ResponseEntity<Any>

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        error {
            status = HttpStatus.BAD_REQUEST.value()
        } as ResponseEntity<Any>

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        error {
            status = HttpStatus.BAD_REQUEST.value()
        } as ResponseEntity<Any>

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        error {
            status = HttpStatus.BAD_REQUEST.value()
        } as ResponseEntity<Any>

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: java.lang.Exception): ResponseEntity<ApiError> {
        log.error("An unexpected exception has been caught")
        log.error(ex.stackTraceToString())
        return error {  }
    }
    
}