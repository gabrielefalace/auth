package com.falace.auth.user

data class User(val email: String, var hashedPassword: String, var verified: Boolean = false) {
    var key: String? = null
}