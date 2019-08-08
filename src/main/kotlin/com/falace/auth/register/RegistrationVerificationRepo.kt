package com.falace.auth.register

import org.springframework.data.mongodb.repository.MongoRepository

interface RegistrationVerificationRepo : MongoRepository<RegistrationVerification, String> {
    fun findByEmail(email: String): List<RegistrationVerification>
    fun deleteByEmail(email: String)
}