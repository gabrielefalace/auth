package com.falace.auth.register

import com.falace.auth.user.UserDto
import com.falace.auth.user.UserService
import com.falace.auth.utils.composeEmail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class RegistrationController(
        val registrationService: RegistrationService,
        val userService: UserService
) {


    @Value("\${server.address}")
    var address: String = ""

    @Value("\${server.port}")
    var port: String = ""

    @Autowired
    lateinit var mailSender: JavaMailSender

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    fun register(@RequestBody userDto: UserDto): String {
        require(!registrationService.isAlreadyRegistered(userDto.email)) {
            "Error: user ${userDto.email} is already registered!"
        }
        registrationService.registerUser(userDto.email, userDto.password)
        val token = UUID.randomUUID()
        val url = "http://$address:$port/verify-registration?email=${userDto.email}&token=$token"
        val text = "To verify your registration click $url"
        val email = composeEmail(userDto.email, "Please verify your email", text)
        mailSender.send(email)
        registrationService.deletePendingRegistrationVerification(userDto.email)
        registrationService.saveRegistrationVerification(RegistrationVerification(userDto.email, token.toString()))
        return url
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

}