package com.falace.auth

import com.falace.auth.user.UserDto
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.net.URL

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-verification")
class RegistrationVerificationTests {

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

    @BeforeEach
    fun setup() {
        db = mongoClient.getDatabase("auth-falace")
        db.drop()
    }

    @Disabled("Requires configuring Email credentials / use for manual tests until automated")
    @Test
    fun `Simple registration with verification should work`() {
        val url = URL("http://$address:$port/register").toString()
        val userData = UserDto(USER_EMAIL, PASSWORD)
        val response = restTemplate.postForEntity(url, userData, String::class.java)
        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)
        val verificationLinkParams = response.body!!.split("?")[1]
        val verifyResponse =
            restTemplate.getForEntity("http://$address:$port/verify-registration?" + verificationLinkParams, Any::class.java)
        Assertions.assertEquals(HttpStatus.OK, verifyResponse.statusCode)
    }


}