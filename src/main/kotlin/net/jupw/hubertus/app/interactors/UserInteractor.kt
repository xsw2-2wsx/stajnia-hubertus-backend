package net.jupw.hubertus.app.interactors

import net.jupw.hubertus.app.data.converters.toUser
import net.jupw.hubertus.app.data.entities.RoleEntity
import net.jupw.hubertus.app.data.entities.UserEntity
import net.jupw.hubertus.app.data.repositories.UserRepository
import net.jupw.hubertus.app.entities.Role
import net.jupw.hubertus.app.entities.RoleImpl
import net.jupw.hubertus.app.entities.User
import net.jupw.hubertus.app.exceptions.*
import net.jupw.hubertus.app.security.Authorities
import net.jupw.hubertus.app.security.TokenService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.Context
import java.security.SecureRandom
import java.util.*
import javax.transaction.Transactional

@Service
class UserInteractor : UserDetailsService {

    companion object {
        const val GENERATED_PASSWD_LEN = 9
        private val log: Logger = LogManager.getLogger()
    }

    private val random = SecureRandom()

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var roleInteractor: RoleInteractor

    @Value("\${storage.profile.pictures}")
    private lateinit var profilePictureLocation: String

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @Autowired
    private lateinit var templateEngine: ITemplateEngine

    @Autowired
    private lateinit var emailSender: JavaMailSender


    val authenticatedUser: User
        get() = SecurityContextHolder.getContext().authentication.principal as User

    fun findAllUsers(): List<User> = userRepository.findAll().map { it.toUser() }

    fun findUserById(id: Int): User =
        userRepository.findByIdOrNull(id)?.toUser()?: throw UserNotFoundException(id)

    fun userExists(username: String): Boolean = Optional.ofNullable(userRepository.findByName(username)).isPresent

    override fun loadUserByUsername(username: String?): User =
        userRepository.findByName(username?: "")?.toUser()?:
        throw UsernameNotFoundException("User with username $username not found")

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    fun createUser(username: String): String {
        if(userExists(username)) throw UserAlreadyExistException(username)

        val password = secRandomString(GENERATED_PASSWD_LEN)

        userRepository.save(UserEntity(
            0, username, passwordEncoder.encode(password), null, null, false, mutableSetOf(), null
        ))

        return password
    }

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    @Transactional
    fun modifyUser(id: Int, name: String, email: String?, phone: String?, isLocked: Boolean) {
        val userToEdit = findUserEntity(id)
        val userWithUsername = userRepository.findByName(name)

        if(userWithUsername != null && userWithUsername.id != userToEdit.id)
            throw UserAlreadyExistException(name)

        userToEdit.let {
            it.name = name
            it.email = email
            it.phone = phone
            it.isLocked = isLocked
        }

        log.info("User ${userToEdit.name} (id ${userToEdit.id}) has been modified by ${authenticatedUser.name}")
    }

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    fun deleteUser(id: Int) {
        userRepository.deleteById(id)
        log.info("User with id $id has been deleted by user with username ${ authenticatedUser.name }")
    }

    @Transactional
    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).RESET_ANY_PASSWORD)")
    fun resetPassword(id: Int): String {
        val user = findUserEntity(id)
        val newPassword = secRandomString(GENERATED_PASSWD_LEN)
        user.password = passwordEncoder.encode(newPassword)
        log.info("Password of user ${ user.name } has been reset by ${ authenticatedUser.name }")
        return newPassword
    }

    @Transactional
    fun changePassword(oldPassword: String, newPassword: String) {
        val user = findUserEntity(authenticatedUser.id)
        if(!passwordEncoder.matches(oldPassword, user.password)) throw PasswordsDoNotMatchException(user.id)
        user.password = passwordEncoder.encode(newPassword)
    }

    fun sendPasswordRecoveryEmail(username: String) {
        val user = userRepository.findByName(username)?: return
        val email = user.email?: return

        val passwordRecoveryToken = tokenService.createPasswordRecoveryAuthToken(user.id)

        val templateContext = Context().apply {
            setVariable("username", user.name)
            setVariable("token", passwordRecoveryToken)
        }

        val messageContentHtml: String = templateEngine.process("password-recovery-email.html", templateContext)

        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")
        helper.setText(messageContentHtml, true)
        helper.setSubject("Password recovery")
        helper.setFrom("noreply@jupw.net")
        helper.setTo(email)

        emailSender.send(message)

    }

    @Transactional
    fun recoverPassword(newPassword: String) {
        if(!tokenService.isPasswordRecoveryToken()) throw AccessDeniedException("Not a password recovery token")

        val user = findUserEntity(authenticatedUser.id)
        user.password = passwordEncoder.encode(newPassword)
    }

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    @Transactional
    fun addRole(userId: Int, roleId: Int) {
        val role = roleInteractor.findRoleEntity(roleId)
        val user = findUserEntity(userId)

        user.roles.add(role)
    }

    @PreAuthorize("hasAuthority(T(net.jupw.hubertus.app.security.Authorities).MANAGE_USERS)")
    @Transactional
    fun removeRole(userId: Int, roleId: Int) {
        val user = findUserEntity(userId)
        user.roles.removeIf { it.id == roleId }
    }

    fun getUserRole(userId: Int, roleId: Int): Role =
        findUserEntity(userId)
            .roles
            .find { it.id == roleId }
            ?.toRole()
            ?: throw UserDoesNotHaveSpecifiedRoleException(userId, roleId)

    @Transactional
    fun setProfilePicture(image: Resource) {
        val user = findUserEntity(authenticatedUser.id)
        val relativePath = "${user.id}.jpg"
        val absolutePath = "$profilePictureLocation/$relativePath"

        val fileResource = resourceLoader.getResource(absolutePath)
        if(!fileResource.isFile) throw IllegalStateException()
        val file = fileResource.file

        val fileOutputStream = file.outputStream()
        val imageInputStream = image.inputStream

        imageInputStream.copyTo(fileOutputStream)
        imageInputStream.close()
        fileOutputStream.close()

        user.profilePicturePath = relativePath
    }

    @Transactional
    fun deleteProfilePicture() {
        val user = findUserEntity(authenticatedUser.id)
        val relativePath = user.profilePicturePath?: throw NoProfilePictureException(user.id)
        val absolutePath = "$profilePictureLocation/$relativePath"

        val fileResource = resourceLoader.getResource(absolutePath)
        if(!fileResource.isFile) throw IllegalStateException()

        val file = fileResource.file
        if(file.exists())
            file.delete()

        user.profilePicturePath = null
    }

    @Transactional
    fun editProfile(email: String?, phone: String?) {
        val user = findUserEntity(authenticatedUser.id)
        user.email = email
        user.phone = phone
    }

    fun getProfilePicture(userId: Int): Resource {
        val path = findUserEntity(userId).profilePicturePath?: throw NoProfilePictureException(userId)
        return resourceLoader.getResource("$profilePictureLocation/$path")
    }

    private fun findUserEntity(id: Int) = userRepository.findByIdOrNull(id)?: throw UserNotFoundException(id)

    private fun secRandomString(len: Int) =
        Base64.getEncoder().encodeToString(ByteArray(len).also { random.nextBytes(it) })

    fun getUserRoles(userId: Int): Set<Role> =
        findUserEntity(userId).roles.map { it.toRole() }.toSet()

    fun RoleEntity.toRole() = RoleImpl(id, name, description, authorities.map { Authorities.readAuthority(it) }.toSet())

}