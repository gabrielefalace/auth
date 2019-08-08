package com.falace.auth.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Instant
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

const val BCRYPT_STRENGTH = 12

val bCryptPasswordEncoder = BCryptPasswordEncoder(BCRYPT_STRENGTH)

fun decodeJwt(jwt: String, signingKey: String): Claims {
    return Jwts.parser()
            .setSigningKey(DatatypeConverter.parseBase64Binary(signingKey))
            .parseClaimsJws(jwt).body
}

fun createJWT(id: String, issuer: String, subject: String, secretKey: String, ttlMillis: Long = 300_000): String {

    val signatureAlgorithm = SignatureAlgorithm.HS256

    val nowMillis = Instant.now().toEpochMilli()

    val apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey)
    val signingKey = SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.jcaName)

    val builder = Jwts.builder().setId(id)
            .setIssuedAt(Date(nowMillis))
            .setSubject(subject)
            .setIssuer(issuer)
            .signWith(signingKey, signatureAlgorithm)

    if (ttlMillis > 0) {
        val expMillis = nowMillis + ttlMillis
        val exp = Date(expMillis)
        builder.setExpiration(exp)
    }

    return builder.compact()
}