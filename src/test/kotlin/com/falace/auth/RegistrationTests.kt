package com.falace.auth

import com.falace.auth.user.UserDto
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URL

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistrationTests {

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

    @Test
    fun `Simple registration should work`() {
        val url = URL("http://$address:$port/register").toString()
        val userData = UserDto(USER_EMAIL, PASSWORD)
        val response = restTemplate.postForEntity(url, userData, String::class.java)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }


    @Test
    fun `Simple registration with verification should work`() {
        val url = URL("http://$address:$port/register").toString()
        val userData = UserDto(USER_EMAIL, PASSWORD)
        val response = restTemplate.postForEntity(url, userData, String::class.java)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        val verificationLinkParams = response.body!!.split("?")[1]
        val verifyResponse =
                restTemplate.getForEntity("http://$address:$port/verify-registration?" + verificationLinkParams, Any::class.java)
        Assertions.assertEquals(HttpStatus.OK, verifyResponse.statusCode)
    }

    @Test
    fun `Registering twice the same email should fail`() {
        val url = URL("http://$address:$port/register").toString()
        val userData = UserDto(USER_EMAIL, PASSWORD)
        val response = restTemplate.postForEntity(url, userData, String::class.java)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        val response2 = restTemplate.postForEntity(url, userData, String::class.java)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.statusCode)
    }

}
