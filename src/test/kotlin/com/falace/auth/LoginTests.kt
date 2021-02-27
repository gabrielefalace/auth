package com.falace.auth

import com.falace.auth.user.User
import com.falace.auth.user.UserDto
import com.falace.auth.utils.BCRYPT_STRENGTH
import com.falace.auth.utils.decodeJwt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.net.URL

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LoginTests {

    val USER_EMAIL = "gabrielefalace@gmail.com"
    val PASSWORD = "pollo-666"

    var address: String = "localhost"

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `get me some Bcrypt hashes`(){
        val hash = BCryptPasswordEncoder(BCRYPT_STRENGTH).encode("fagiano-666")
        println(" Hash is: $hash")
    }


    @Test
    fun `login with good credential should succeed`() {
        val url = URL("http://$address:$port/login").toString()
        val userData = UserDto(USER_EMAIL, PASSWORD)
        val response = restTemplate.postForEntity(url, userData, String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        val claims = decodeJwt(response.body!!, "somepwdsomepwdsomepwdsomepwdsomepwdsomepwdsomepwdsomepwdsomepwdsomepwd")
        assertEquals("Auth-Falace", claims.issuer)
        assertEquals(USER_EMAIL, claims.subject)
    }

    @Test
    fun `login with bad credential should fail`() {
        val url = URL("http://$address:$port/login").toString()
        val userData = UserDto(USER_EMAIL, "TOBLERONE")
        val response = restTemplate.postForEntity(url, userData, String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

}
