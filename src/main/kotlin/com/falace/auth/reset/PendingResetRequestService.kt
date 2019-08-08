package com.falace.auth.reset

import org.springframework.stereotype.Service

@Service
class PendingResetRequestService(private val pendingResetRequestRepo: PendingResetRequestRepo) {


    fun findByEmail(email: String): PendingResetRequest {
        val foundRequests = pendingResetRequestRepo.findByEmail(email)
        require(foundRequests.size == 1) { "No proper pending reset request for $email found" }
        return foundRequests.first()
    }

    fun save(pendingResetRequest: PendingResetRequest) = pendingResetRequestRepo.save(pendingResetRequest)

    fun deleteAllMatching(email: String) = pendingResetRequestRepo.deleteByEmail(email)
}