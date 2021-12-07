package com.falace.auth.social

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
class FacebookConnectController {

    @Value("\${social.facebook.appToken}")
    var appToken: String = ""

    @PostMapping("/connect-facebook")
    fun connectFacebook(
        @RequestParam userAccessToken: String,
        @RequestParam email: String
    ) {

        val client = HttpClient.newHttpClient()
        val request =
            HttpRequest.newBuilder(URI("https://graph.facebook.com/debug_token?input_token=$userAccessToken&access_token=$appToken"))
        val response = client.send(request.build(), HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200 until 300)
            throw IllegalAccessException("Facebook login not successful!")
    }

}