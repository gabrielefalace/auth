import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts

fun decodeJwt(jwt: String, hmacSecret: String): Claims =
    Jwts.parser().setSigningKey(hmacSecret.toByteArray()).parseClaimsJws(jwt).body
