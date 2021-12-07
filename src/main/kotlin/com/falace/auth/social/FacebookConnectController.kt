package com.falace.auth.social

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
class FacebookConnectController {

    @Value("\${social.facebook.appToken}")
    var appToken: String = ""

    @GetMapping("/connect-facebook")
    fun connectFacebook(
        @RequestParam("input-token") userAccessToken: String,
        @RequestParam email: String
    ) {

        val response = RestTemplate().getForEntity(
            URI("https://graph.facebook.com/debug_token?input_token=$userAccessToken&access_token=$appToken"),
            Any::class.java
        )

        if (response.statusCode.value() !in 200 until 300)
            throw IllegalAccessException("Facebook login not successful!")
    }

}