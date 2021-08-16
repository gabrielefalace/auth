package com.falace.auth.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.HS512
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Instant
import java.util.*
import javax.crypto.spec.SecretKeySpec

const val ISSUER = "Wino-Auth"
const val BCRYPT_STRENGTH = 12

val bCryptPasswordEncoder = BCryptPasswordEncoder(BCRYPT_STRENGTH)


/**
 * @param id Usually a UUID random ID for the token
 * @param issuer This application e.g. Auth-Falace
 * @param subject The “principal” e.g. the email/username of the JWT holder
 * @param hmacSecret
 */
fun createJWT(id: String, issuer: String, subject: String, hmacSecret: String, ttlMillis: Long = 600_000): String {
    require(ttlMillis > 0)
    val now = Instant.now()
    val claims = TreeMap<String, String>()
    claims["role"] = "user"

    return Jwts.builder()
        .setClaims(claims as Map<String, Any>?)
        .setId(id)
        .setIssuedAt(Date.from(now))
        .setSubject(subject)
        .setIssuer(issuer)
        .signWith(SecretKeySpec(hmacSecret.toByteArray(), HS512.jcaName), HS512)
        .setExpiration(Date.from(now.plusSeconds(126_230_400)))
        .compact()
}