package com.falace.auth.social

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.lang.IllegalArgumentException
import java.net.URI


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
class FacebookConnectController {

    private val facebookBaseUrl = "https://graph.facebook.com"

    @Value("\${social.facebook.appToken}")
    var appToken: String = ""

    @GetMapping("/connect-facebook")
    fun connectFacebook(
        @RequestParam("user-token") userAccessToken: String,
        @RequestParam email: String
    ) : String {
        //val appAccessToken = appToken.replace("|", "%7C")
        val facebookValidationEndpoint = "$facebookBaseUrl/me?access_token=$userAccessToken"
        val response = RestTemplate().getForEntity(URI(facebookValidationEndpoint), MeResponse::class.java)

        if (response.statusCode.value() !in 200 until 300 || response.body == null)
            throw IllegalAccessException("Facebook login not successful!")

        val user = response.body as MeResponse

        val facebookAdditionalDataEndpoint =
            "$facebookBaseUrl/${user.id}?fields=name,email&access_token=$userAccessToken"
        val profileDataResponse =
            RestTemplate().getForObject(URI(facebookAdditionalDataEndpoint), UserResponse::class.java)

        return profileDataResponse?.email ?: throw IllegalArgumentException("User doesn't have an email!")

        //TODO: lookup email; if not found, add user to the DB; either way, issue&return a token

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