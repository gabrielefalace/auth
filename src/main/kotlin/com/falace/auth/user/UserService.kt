package com.falace.auth.user

import org.springframework.stereotype.Service

@Service
class UserService(private val userRepo: UserRepo) {

    fun findSingleUserByEmail(email: String, onlyVerified: Boolean = false): User {
        val matchingUsers = userRepo.findByEmail(email)
        require(matchingUsers.size == 1) { "No single account for $email" }
        val user = matchingUsers.first()
        if (onlyVerified && user.verified || !onlyVerified)
            return user
        throw IllegalArgumentException("No verified account found for $email")
    }

    fun setUserVerified(user: User) {
        user.verified = true
        userRepo.save(user)
    }

}