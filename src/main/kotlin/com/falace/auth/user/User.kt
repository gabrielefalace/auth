package com.falace.auth.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(val email: String, var hashedPassword: String, var verified: Boolean = false) {

    @Id
    var key: String? = null

}