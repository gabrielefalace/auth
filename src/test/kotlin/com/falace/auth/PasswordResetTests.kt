package com.falace.auth

import com.falace.auth.reset.PendingResetRequestRepo
import com.falace.auth.user.User
import com.falace.auth.user.UserRepo
import com.falace.auth.utils.bCryptPasswordEncoder
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.patchForObject
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URI

@Disabled("Requires configuring Email credentials / use for manual tests until automated")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PasswordResetTests {

    val USER_EMAIL = "gabriele.falace@gmail.com"
    val PASSWORD = "pollo"

    @Value("\${server.address}")
    var address: String = ""

    @LocalServerPort
    var port: Int = 0

    lateinit var db: MongoDatabase

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var mongoClient: MongoClient

    @Autowired
    lateinit var pendingResetRequestRepo: PendingResetRequestRepo

    @Autowired
    lateinit var userRepo: UserRepo

    @BeforeEach
    fun setup() {
        db = mongoClient.getDatabase("auth-falace")
        db.drop()
    }

    @Test
    fun `Password-reset request should fail for a non registered user`() {
        val response = restTemplate.getForEntity("http://$address:$port/reset?email=$USER_EMAIL", Any::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val pendingRequests = pendingResetRequestRepo.findByEmail(USER_EMAIL)
        assertEquals(pendingRequests.size, 0)
    }

    @Test
    fun `Password-reset request should succeed for a registered and verified user`() {
        userRepo.save(User(email = USER_EMAIL, hashedPassword = PASSWORD, verified = true))
        val response = restTemplate.getForEntity("http://$address:$port/reset?email=$USER_EMAIL", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        val pendingRequests = pendingResetRequestRepo.findByEmail(USER_EMAIL)
        assertEquals(pendingRequests.size, 1)
    }

    @Test
    fun `Password should be reset successfully`() {

        userRepo.save(User(email = USER_EMAIL, hashedPassword = PASSWORD, verified = true))
        val response = restTemplate.getForEntity("http://$address:$port/reset?email=$USER_EMAIL", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        val verificationLinkParams = response.body!!.split("?")[1]
        val url = URI("http://$address:$port/reset?$verificationLinkParams&newPassword=mickeymouse")
        val resetResponse = restTemplate.patchForObject<Unit>(url, Any::class.java)

        val newHashedPassword = userRepo.findByEmail(USER_EMAIL).first().hashedPassword
        assertTrue(bCryptPasswordEncoder.matches("mickeymouse", newHashedPassword))
    }


}
