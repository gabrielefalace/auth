package com.falace.auth.register

import com.falace.auth.user.UserDto
import com.falace.auth.user.UserService
import com.falace.auth.utils.composeEmail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.web.bind.annotation.*
import java.net.InetAddress
import java.util.*


@RestController
class RegistrationController(
        val registrationService: RegistrationService,
        val userService: UserService
) {

    @Value("\${auth.registration.emailVerification}")
    var isEmailVerificationRequired = false

    var address: String = InetAddress.getLocalHost().hostAddress

    @Value("\${server.port}")
    var port: Int = 80

    @Autowired
    lateinit var mailSender: JavaMailSender

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody userDto: UserDto): String {
        require(!registrationService.isAlreadyRegistered(userDto.email)) {
            "Error: user ${userDto.email} is already registered!"
        }
        registrationService.registerUser(userDto.email, userDto.password)
        return if(isEmailVerificationRequired)
            requestEmailVerification(userDto) else ""
    }

    @GetMapping("/verify-registration")
    fun verifyRegistration(@RequestParam email: String, @RequestParam token: String) {
        val registrationVerification = registrationService.findRegistrationVerification(email)
        require(registrationVerification.token == token) {
            "Token $token for $email not found"
        }
        val user = userService.findSingleUserByEmail(email)
        userService.setUserVerified(user)
        registrationService.deletePendingRegistrationVerification(email)
    }


    internal fun requestEmailVerification(userDto: UserDto): String {
        val token = UUID.randomUUID()
        val url = "http://$address:$port/verify-registration?email=${userDto.email}&token=$token"
        val text = "To verify your registration click $url"
        val email = composeEmail(userDto.email, "Please verify your email", text)
        mailSender.send(email)
        registrationService.deletePendingRegistrationVerification(userDto.email)
        registrationService.saveRegistrationVerification(RegistrationVerification(userDto.email, token.toString()))
        return url
    }
}