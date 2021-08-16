package com.falace.auth.login

import com.falace.auth.reset.PendingResetRequestService
import com.falace.auth.user.UserDto
import com.falace.auth.user.UserService
import com.falace.auth.utils.ISSUER
import com.falace.auth.utils.bCryptPasswordEncoder
import com.falace.auth.utils.createJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class LoginController(val userService: UserService, val pendingResetRequestService: PendingResetRequestService) {

    @Value("\${auth.login.restrictVerified}")
    var onlyVerifiedUsers: Boolean = false

    @Value("\${auth.secretKey}")
    var secretKey: String = ""

    @PostMapping("/login")
    fun login(@RequestBody userDto: UserDto): String {
        val user = userService.findSingleUserByEmail(userDto.email, onlyVerifiedUsers)
        if (bCryptPasswordEncoder.matches(userDto.password, user.hashedPassword)) {
            pendingResetRequestService.deleteAllMatching(userDto.email)
            return createJWT(UUID.randomUUID().toString(), ISSUER, user.email, secretKey)
        }
        throw IllegalAccessException("Bad Credentials!")
    }


}