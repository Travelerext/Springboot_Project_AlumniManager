package jmu.lwk.alumnimanager.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {

    private val bcrypt = BCryptPasswordEncoder()

    fun encode(value: String): String = bcrypt.encode(value)

    fun matches(value: String, hashed: String) = bcrypt.matches(value, hashed)
}