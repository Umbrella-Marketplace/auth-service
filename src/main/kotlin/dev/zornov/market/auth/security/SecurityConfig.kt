package dev.zornov.market.auth.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    @Value($$"${app.jwt.secret}") private val jwtSecret: String
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()) } }
            .build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val key = SecretKeySpec(jwtSecret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        return NimbusJwtDecoder.withSecretKey(key).build()
    }

    private fun jwtAuthConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
            val roles = jwt.getClaim<List<String>>("roles") ?: emptyList()
            roles.map { role -> SimpleGrantedAuthority("ROLE_$role") }
        }
        return converter
    }
}
