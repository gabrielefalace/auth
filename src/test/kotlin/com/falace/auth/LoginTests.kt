package com.falace.auth

import com.falace.auth.user.User
import com.falace.auth.user.UserDto
import com.falace.auth.user.UserRepo
import com.falace.auth.utils.ISSUER
import com.falace.auth.utils.bCryptPasswordEncoder
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import decodeJwt
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.net.URL
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginTests {

    val USER_EMAIL = "gabriele.falace@gmail.com"
    val PASSWORD = "pollo"

    @Value("\${server.address}")
    var address: String = ""

    @Value("\${auth.secretKey}")
    var secretKey: String = ""

    @LocalServerPort
    var port: Int = 0

    lateinit var db: MongoDatabase

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var mongoClient: MongoClient

    @Autowired
    lateinit var userRepo: UserRepo


    @BeforeEach
    fun setup() {
        db = mongoClient.getDatabase("auth-falace")
        db.drop()
    }


    @Test
    fun `login with good credential should succeed`() {
        userRepo.insert(User(USER_EMAIL, bCryptPasswordEncoder.encode(PASSWORD)))
        val url = URL("http://$address:$port/login").toString()
        val userData = UserDto(USER_EMAIL, PASSWORD)
        val response = restTemplate.postForEntity(url, userData, String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        val claims = decodeJwt(response.body!!, secretKey)
        assertEquals(ISSUER, claims.issuer)
        assertEquals(USER_EMAIL, claims.subject)
    }

    @Test
    fun `login with bad credential should fail`() {
        userRepo.insert(User(USER_EMAIL, bCryptPasswordEncoder.encode(PASSWORD)))
        val url = URL("http://$address:$port/login").toString()
        val userData = UserDto(USER_EMAIL, "TOBLERONE")
        val response = restTemplate.postForEntity(url, userData, String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

}
