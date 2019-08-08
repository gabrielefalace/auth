package com.falace.auth.reset

import org.springframework.data.annotation.Id

data class PendingResetRequest(val email: String, val token: String, val timestamp: Long) {
    @Id
    var key: String? = null
}