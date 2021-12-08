package com.falace.auth.social

import com.falace.auth.register.RegistrationService
import com.falace.auth.user.UserService
import com.falace.auth.utils.ISSUER
import com.falace.auth.utils.createJWT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.util.*


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
class FacebookConnectController {

    private val facebookBaseUrl = "https://graph.facebook.com"

    @Value("\${auth.secretKey}")
    var secretKey: String = ""

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var registrationService: RegistrationService

    @GetMapping("/connect-facebook")
    fun connectFacebook(
        @RequestParam("user-token") userAccessToken: String
    ): String {
        val facebookValidationEndpoint = "$facebookBaseUrl/me?access_token=$userAccessToken"
        val response = RestTemplate().getForEntity(URI(facebookValidationEndpoint), MeResponse::class.java)

        if (response.statusCode.value() !in 200 until 300 || response.body == null)
            throw IllegalAccessException("Facebook login not successful!")

        val user = response.body as MeResponse

        val facebookAdditionalDataEndpoint =
            "$facebookBaseUrl/${user.id}?fields=name,email&access_token=$userAccessToken"
        val profileDataResponse =
            RestTemplate().getForObject(URI(facebookAdditionalDataEndpoint), UserResponse::class.java)

        val fbEmail = profileDataResponse?.email ?: throw IllegalArgumentException("User doesn't have an email!")

        val registeredUser = try {
            userService.findSingleUserByEmail(fbEmail)
        } catch (e: Exception) {
            registrationService.registerExternalUser(fbEmail, user.name, "Facebook", user.id.toString())
        }

        return createJWT(UUID.randomUUID().toString(), ISSUER, fbEmail, secretKey)

    }

}

data class MeResponse(
    val id: Long = -1,
    val name: String = ""
)

data class UserResponse(
    val id: Long = -1,
    val name: String = "",
    val email: String = ""
)