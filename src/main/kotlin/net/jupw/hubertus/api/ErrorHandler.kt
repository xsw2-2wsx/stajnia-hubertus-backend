package net.jupw.hubertus.api

import net.jupw.hubertus.api.models.ApiError
import net.jupw.hubertus.api.models.ApiSubError
import net.jupw.hubertus.app.exceptions.ActivityDoesNotExistException
import net.jupw.hubertus.app.exceptions.UserAlreadyExistException
import net.jupw.hubertus.app.exceptions.UserNotFoundException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
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

    private fun error(
        status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        message: String = UNKNOWN_ERROR_MESSAGE,
        suggestedAction: String = CONTACT_ADMIN_SUGGESTED_ACTION,
        subErrors: List<ApiSubError> = emptyList(),
    ): ResponseEntity<ApiError> =
        ApiError(status.value(), message, suggestedAction, subErrors = subErrors).toResponseEntity(status = status)


    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
    ) as ResponseEntity<Any>

    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
    ) as ResponseEntity<Any>

    override fun handleHttpMediaTypeNotAcceptable(
        ex: HttpMediaTypeNotAcceptableException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    override fun handleServletRequestBindingException(
        ex: ServletRequestBindingException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    override fun handleConversionNotSupported(
        ex: ConversionNotSupportedException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    override fun handleMissingServletRequestPart(
        ex: MissingServletRequestPartException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    override fun handleBindException(
        ex: BindException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error (
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    override fun handleAsyncRequestTimeoutException(
        ex: AsyncRequestTimeoutException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        webRequest: WebRequest
    ): ResponseEntity<Any>? = error (
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        body: Any?,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = error(
        status = HttpStatus.BAD_REQUEST
        ) as ResponseEntity<Any>

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException) =
        error(
            status = HttpStatus.NOT_FOUND,
            message = "Podany użytkownik nie istnieje",
            suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
        )

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<ApiError> =
        error (
            status = HttpStatus.UNAUTHORIZED,
            message = "Nieprawidłowa nazwa użytkownika lub hasło",
            suggestedAction = "Upewnij się, że CapsLock jest wyłączony i spróbuj ponownie",
        )

    @ExceptionHandler(DisabledException::class)
    fun handleAccountDisabled(ex: DisabledException): ResponseEntity<ApiError> =
        error (
            status = HttpStatus.FORBIDDEN,
            message = "Twoje konto zostało wyłączone",
        )

    @ExceptionHandler(LockedException::class)
    fun handleAccountLocked(ex: LockedException) =
        error (
            status = HttpStatus.FORBIDDEN,
            message = "Twoje konto zostało zablokowane",
        )

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException) =
        error (
            status = HttpStatus.FORBIDDEN,
            message = "Nie masz uprawnień do wykonania tej czynności",
            suggestedAction = "Skontaktuj się z administratorem w celu nadania uprawnień",
        )

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ApiError> {
        val subErrorCollection = ex.constraintViolations.map { ApiSubError(
            message = "Nieprawidłowa wartość: ${it.invalidValue} - ${it.message}",
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION
        )}

        return error (
            status = HttpStatus.BAD_REQUEST,
            message = "Nieprawidłowe dane",
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION,
            subErrors = subErrorCollection,
        )
    }

    @ExceptionHandler(UserAlreadyExistException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistException) =
        error (
            status = HttpStatus.CONFLICT,
            message = "Użytkownik o tej nazwie już istnieje",
            suggestedAction = "Wybierz inną nazwę",
        )

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val apiSubErrors: MutableList<ApiSubError> = LinkedList()

        ex.bindingResult.fieldErrors.forEach {
            apiSubErrors.add(ApiSubError (
                    message = "Nieprawidłowa wartość: ${it.rejectedValue} - ${it.defaultMessage}",
                    suggestedAction = RECHECK_DATA_SUGGESTED_ACTION,
                ))
        }

        return error (
            status = HttpStatus.BAD_REQUEST,
            message = "Nieprawidłowe dane",
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION,
            subErrors = apiSubErrors,
        ) as ResponseEntity<Any>
    }

    @ExceptionHandler(ActivityDoesNotExistException::class)
    fun handleActivityDoesNotExistException(ex: ActivityDoesNotExistException) =
        error (
            status = HttpStatus.NOT_FOUND,
            message = "Aktywność nie istnieje",
            suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
        )

    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        error (
            status = HttpStatus.BAD_REQUEST,
        ) as ResponseEntity<Any>

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        error (
            status = HttpStatus.BAD_REQUEST,
        ) as ResponseEntity<Any>

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        error (
            status = HttpStatus.BAD_REQUEST,
        ) as ResponseEntity<Any>

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        httpStatus: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        error (
            status = HttpStatus.BAD_REQUEST,
        ) as ResponseEntity<Any>

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: java.lang.Exception): ResponseEntity<ApiError> {
        log.error("An unexpected exception has been caught")
        log.error(ex.stackTraceToString())
        return error()
    }
    
}