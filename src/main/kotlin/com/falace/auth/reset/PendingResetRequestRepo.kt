package com.falace.auth.reset

import org.springframework.data.mongodb.repository.MongoRepository

interface PendingResetRequestRepo : MongoRepository<PendingResetRequest, String> {
    fun findByEmail(email: String): List<PendingResetRequest>
    fun deleteByEmail(email: String)
}