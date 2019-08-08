package com.falace.auth.register

import org.springframework.data.annotation.Id

data class RegistrationVerification(val email: String, val token: String) {
    @Id
    var key: String? = null
}