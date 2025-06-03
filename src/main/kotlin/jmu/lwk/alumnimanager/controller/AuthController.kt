package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.security.AuthService
import org.springframework.web.bind.annotation.*

@RestController

@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    data class AuthRequest(
        val name: String,
        val password: String
    )

    data class RefreshToken(val refreshToken: String)

    @PostMapping("/register")
    fun register(@RequestBody body: AuthRequest) {
        authService.register(body.name, body.password)
    }

    @PostMapping("/login")
    fun login(@RequestBody body: AuthRequest) = authService.login(body.name, body.password)

    @PostMapping("/refresh")
    fun refresh(@RequestBody body: RefreshToken) = authService.refresh(body.refreshToken)
}