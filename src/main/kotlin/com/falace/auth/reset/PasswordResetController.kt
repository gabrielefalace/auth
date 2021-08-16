package com.falace.auth.reset

import com.falace.auth.register.RegistrationService
import com.falace.auth.user.UserService
import com.falace.auth.utils.composeEmail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.web.bind.annotation.*
import java.net.InetAddress
import java.time.Instant
import java.util.*

@RestController
class PasswordResetController(
        val pendingResetRequestService: PendingResetRequestService,
        val registrationService: RegistrationService,
        val userService: UserService
) {

    val SECONDS_2_DAYS = 172_800

    var address: String = InetAddress.getLocalHost().hostAddress

    @Value("\${server.port}")
    var port: String = ""

    @Autowired
    lateinit var mailSender: JavaMailSender

    @GetMapping("/reset")
    @ResponseStatus(HttpStatus.OK)
    fun requestPasswordReset(@RequestParam email: String): String {
        val token = UUID.randomUUID()
        userService.findSingleUserByEmail(email)
        val resetLink = "http://$address:$port/reset?email=${email}&token=$token"
        val text = "Reset your password here $resetLink"
        val msg = composeEmail(email, "Your password reset request", text)
        mailSender.send(msg)
        pendingResetRequestService.save(PendingResetRequest(email, token.toString(), Instant.now().epochSecond))
        return resetLink
    }

    @PatchMapping("/reset")
    fun resetPassword(@RequestParam email: String, @RequestParam newPassword: String, @RequestParam token: String) {
        val matchingRequest = pendingResetRequestService.findByEmail(email)
        require(matchingRequest.token == token) { "No pw reset request for $email token=$token" }
        require(isResetLinkNotOlderThan2Days(matchingRequest)) { "Sorry your reset link is too old!" }
        registrationService.updateUser(email, newPassword)
    }

    private fun isResetLinkNotOlderThan2Days(
            matchingRequest: PendingResetRequest
    ) = Instant.now().epochSecond - matchingRequest.timestamp < SECONDS_2_DAYS

}