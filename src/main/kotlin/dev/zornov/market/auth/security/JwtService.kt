package dev.zornov.market.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.zornov.market.auth.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value($$"${jwt.secret}") private val secret: String,
    @Value($$"${jwt.issuer}") private val issuer: String
) {
    private val expirationMs: Long = 3600000
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(user: User): String = JWT.create()
        .withIssuer(issuer)
        .withSubject(user.id)
        .withClaim("name", user.name)
        .withClaim("roles", user.roles.map { it.name })
        .withIssuedAt(Date())
        .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
        .sign(algorithm)
}