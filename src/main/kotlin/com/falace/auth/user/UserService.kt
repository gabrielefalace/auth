package com.falace.auth.user

import org.springframework.stereotype.Service

@Service
class UserService {

    val daniela = User("danielamartino83@gmail.com", "\$2a\$12\$WPxn4.R4X1pzF8Y5dQXpIOmgIbQD6/C3hOM4GIVFGKOFB7Vj7jM4u")
    val gabriele = User("gabrielefalace@gmail.com", "\$2a\$12\$aeD.e6LIDRF/bJrCUg5hFeJ/q5LFunXGn5DHT3tvVJlK37bww7OaO")
    val stefano = User("stefanogelsomino84@gmail.com", "\$2a\$12\$LotQyY6RGshVtk9/SKIQuut3WSGlz48zh8nTyBB.exV0214BXUhCC")

    val users = listOf(daniela, gabriele, stefano)

    fun findSingleUserByEmail(email: String, onlyVerified: Boolean = false): User {
        val user = users.find { it.email == email }
                ?: throw java.lang.IllegalArgumentException("Credential not found")

        if (onlyVerified && user.verified || !onlyVerified)
            return user
        throw IllegalArgumentException("No verified account found for $email")
    }

}