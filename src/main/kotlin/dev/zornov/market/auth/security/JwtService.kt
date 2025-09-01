package dev.zornov.market.auth.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import dev.zornov.market.auth.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JwtService(
    @Value($$"${app.jwt.secret}") private val secret: String,
    @Value($$"${app.jwt.issuer}") private val issuer: String,
    @Value($$"${app.jwt.expiration-ms}") private val expirationMs: Long
) {

    fun generateToken(user: User): String {
        val now = Instant.now()
        val exp = now.plusMillis(expirationMs)

        val claims = JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(user.id)
            .claim("name", user.name)
            .claim("role", user.role)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(exp))
            .build()

        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.HS256).build(),
            claims
        )

        signedJWT.sign(MACSigner(secret))

        return signedJWT.serialize()
    }
}
