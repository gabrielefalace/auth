package com.falace.auth.register

import com.falace.auth.user.User
import com.falace.auth.user.UserRepo
import com.falace.auth.utils.bCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegistrationService(val userRepo: UserRepo, val registrationVerificationRepo: RegistrationVerificationRepo) {

    fun isAlreadyRegistered(userEmail: String) = userRepo.findByEmail(userEmail).isNotEmpty()

    fun registerUser(email: String, password: String) =
            userRepo.insert(User(email, bCryptPasswordEncoder.encode(password)))

    fun updateUser(email: String, password: String) {
        val user = userRepo.findByEmail(email).first()
        user.hashedPassword = bCryptPasswordEncoder.encode(password)
        userRepo.save(user)
    }

    // maybe separate RegistrationVerification to a package of its own...

    fun deletePendingRegistrationVerification(email: String) {
        registrationVerificationRepo.deleteById(email)
    }

    fun findRegistrationVerification(email: String): RegistrationVerification {
        val matching = registrationVerificationRepo.findByEmail(email)
        check(matching.size == 1) { "Error: there should be only one matching verification link out there" }
        return matching.first()
    }

    fun saveRegistrationVerification(registrationVerification: RegistrationVerification) = registrationVerificationRepo.save(registrationVerification)

}