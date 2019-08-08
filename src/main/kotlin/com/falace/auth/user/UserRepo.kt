package com.falace.auth.user

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : MongoRepository<User, String> {
    fun findByEmail(email: String): List<User>
}