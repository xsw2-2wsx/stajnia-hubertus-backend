package net.jupw.hubertus.api

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import net.jupw.hubertus.api.models.ApiError
import net.jupw.hubertus.api.models.ApiSubError
import net.jupw.hubertus.app.configuration.exceptions.ConfigurationGroupDoesNotExistException
import net.jupw.hubertus.app.configuration.exceptions.InvalidConfigurationKeyException
import net.jupw.hubertus.app.exceptions.*
import net.jupw.hubertus.business.exceptions.ActivityTypeNotAllowedException
import net.jupw.hubertus.business.exceptions.InsufficientSpaceException
import net.jupw.hubertus.business.exceptions.NotEnoughPrecedenceException
import net.jupw.hubertus.app.exceptions.RoleDoesNotExistException
import net.jupw.hubertus.util.InvalidTimeException
import net.jupw.hubertus.util.validation.ValidationException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
        private const val CONTACT_ADMIN_SUGGESTED_ACTION = "Prosz?? skontakuj si?? z administratorem systemu"
        private const val CONTACT_IF_ERROR_SUGGESTED_ACTION =
            "Je??li uwa??asz ??e to b????d, skontaktuj si?? z administratorem systemu"
        private const val RECHECK_DATA_SUGGESTED_ACTION = "Sprawd?? poprawno???? wprowadzonych danych"

        private const val UNKNOWN_ERROR_MESSAGE = "Przepraszamy, co?? posz??o nie tak"

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
            message = "Podany u??ytkownik nie istnieje",
            suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
        )

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<ApiError> =
        error (
            status = HttpStatus.UNAUTHORIZED,
            message = "Nieprawid??owa nazwa u??ytkownika lub has??o",
            suggestedAction = "Upewnij si??, ??e CapsLock jest wy????czony i spr??buj ponownie",
        )

    @ExceptionHandler(DisabledException::class)
    fun handleAccountDisabled(ex: DisabledException): ResponseEntity<ApiError> =
        error (
            status = HttpStatus.FORBIDDEN,
            message = "Twoje konto zosta??o wy????czone",
        )

    @ExceptionHandler(LockedException::class)
    fun handleAccountLocked(ex: LockedException) =
        error (
            status = HttpStatus.FORBIDDEN,
            message = "Twoje konto zosta??o zablokowane",
        )

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException) =
        error (
            status = HttpStatus.FORBIDDEN,
            message = "Nie masz uprawnie?? do wykonania tej czynno??ci",
            suggestedAction = "Skontaktuj si?? z administratorem w celu nadania uprawnie??",
        )

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ApiError> {
        val subErrorCollection = ex.constraintViolations.map { ApiSubError(
            message = "Nieprawid??owa warto????: ${it.invalidValue} - ${it.message}",
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION
        )}

        return error (
            status = HttpStatus.BAD_REQUEST,
            message = "Nieprawid??owe dane",
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION,
            subErrors = subErrorCollection,
        )
    }

    @ExceptionHandler(UserAlreadyExistException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistException) =
        error (
            status = HttpStatus.CONFLICT,
            message = "U??ytkownik o tej nazwie ju?? istnieje",
            suggestedAction = "Wybierz inn?? nazw??",
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
                    message = "Nieprawid??owa warto????: ${it.rejectedValue} - ${it.defaultMessage}",
                    suggestedAction = RECHECK_DATA_SUGGESTED_ACTION,
                ))
        }

        return error (
            status = HttpStatus.BAD_REQUEST,
            message = "Nieprawid??owe dane",
            suggestedAction = RECHECK_DATA_SUGGESTED_ACTION,
            subErrors = apiSubErrors,
        ) as ResponseEntity<Any>
    }

    @ExceptionHandler(ActivityDoesNotExistException::class)
    fun handleActivityDoesNotExist(ex: ActivityDoesNotExistException) =
        error (
            status = HttpStatus.NOT_FOUND,
            message = "Aktywno???? nie istnieje",
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

    @ExceptionHandler(ConfigurationGroupDoesNotExistException::class)
    fun handleConfigurationGroupDoesNotExist(ex: ConfigurationGroupDoesNotExistException): ResponseEntity<Any> =
        error(
            status = HttpStatus.BAD_REQUEST,
            message = "Nie znaleziono ????danej konfiguracji"
        ) as ResponseEntity<Any>

    @ExceptionHandler(InvalidConfigurationKeyException::class)
    fun handleInvalidConfigurationKey(ex: InvalidConfigurationKeyException): ResponseEntity<Any> =
        error(
            status = HttpStatus.BAD_REQUEST,
            message = "Nie mo??na by??o ustawi?? tej konfiguracji",
        ) as ResponseEntity<Any>

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException) = error (
        status = HttpStatus.BAD_REQUEST,
        message = "Nieprawid??owe dane - '${ex.report.value}'",
        suggestedAction = "Zapoznaj si?? z b????dami i popraw dane",
        subErrors = ex.report.failedRequirement.map { ApiSubError(
            message = it.message,
            suggestedAction = "popraw dane"
        ) }
    )

    @ExceptionHandler(BookingDoesNotExistException::class)
    fun handleBookingDoesNotExist(ex: BookingDoesNotExistException) = error (
        status = HttpStatus.NOT_FOUND,
        message = "Nie znaleziono ????danej rezerwacji",
        suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
    )

    @ExceptionHandler(NotBookingOwnerException::class)
    fun handleNotBookingOwner(ex: NotBookingOwnerException) = error (
        status = HttpStatus.FORBIDDEN,
        message = "Nie jeste?? w??a??cicielem tej rezerwacji",
        suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
    )

    @ExceptionHandler(BookingHoursNotAllowedException::class)
    fun handleBookingHoursNotAllowed(ex: BookingHoursNotAllowedException) = error (
        status = HttpStatus.BAD_REQUEST,
        message = "Rezerwacja o takich godzinach nie jest dozwolona ze wzgl??du na konfiguracje aplikacji",
        suggestedAction = CONTACT_ADMIN_SUGGESTED_ACTION
    )

    @ExceptionHandler(InvalidTimeException::class)
    fun handleInvalidTime(ex: InvalidTimeException) = error (
        status = HttpStatus.BAD_REQUEST,
        message = "Niepoprawnie sformatowany czas: '${ex.time}'"
    )

    @ExceptionHandler(ActivityTypeNotAllowedException::class)
    fun handleActivityTypeNotAllowed(ex: ActivityTypeNotAllowedException) = error (
        status = HttpStatus.CONFLICT,
        message = "Ten typ aktywno??ci nie jest dozwolony w tych godzinach",
        suggestedAction = "Zmie?? godziny rezerwacji",
    )

    @ExceptionHandler(InsufficientSpaceException::class)
    fun handleInsufficientSpace(ex: InsufficientSpaceException) = error (
        status = HttpStatus.CONFLICT,
        message = "Rezerwacja nie mie??ci si??",
        suggestedAction = "Zmie?? rezerwacje",
    )

    @ExceptionHandler(NotEnoughPrecedenceException::class)
    fun handleNotEnoughPrecedence(ex: NotEnoughPrecedenceException) = error (
        status = HttpStatus.CONFLICT,
        message = """Rezerwacji mo??na dokonywa?? z przynajmniej ${ex.requiredDuration.toMinutes()} 
            | minutowym wyprzedzeniem (obecnie: ${ex.duration.toMinutes()} minut)
            """.trimMargin().replace("\n", ""),
        suggestedAction = "Dokonaj rezerwacji na p????niejszy termin"
    )

    @ExceptionHandler(RoleDoesNotExistException::class)
    fun handleRoleDoesNotExist(ex: RoleDoesNotExistException) = error (
        status = HttpStatus.BAD_REQUEST,
        message = "Rola nie istnieje",
        suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
    )

    @ExceptionHandler(AuthorityDoesNotExistException::class)
    fun handleAuthorityDoesNotExist(ex: AuthorityDoesNotExistException) = error (
        status = HttpStatus.BAD_REQUEST,
        message = "Uprawnienie nie istnieje",
        suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
    )

    @ExceptionHandler(UserDoesNotHaveSpecifiedRoleException::class)
    fun handleUserDoesNotHaveSpecifiedRole(ex: UserDoesNotHaveSpecifiedRoleException) = error (
        status = HttpStatus.BAD_REQUEST,
        message = "Ten u??ytkownik nie ma ????danej roli",
        suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
    )

    @ExceptionHandler(NoProfilePictureException::class)
    fun handleNoProfilePicture(ex: NoProfilePictureException) =
        ResponseEntity.notFound().build<Any>()

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredToken(ex: ExpiredJwtException) = error(
        HttpStatus.UNAUTHORIZED,
        message = "Sesja wygas??a",
        suggestedAction = "Zaloguj si?? ponownie"
    )

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException) = error(
        status = HttpStatus.UNAUTHORIZED,
        message = "Wyst??pi?? b????d uwierzytelnienia",
        suggestedAction = CONTACT_IF_ERROR_SUGGESTED_ACTION,
    )
}