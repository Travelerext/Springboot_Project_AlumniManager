package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.model.User
import jmu.lwk.alumnimanager.repository.AlumniAssociationRepository
import jmu.lwk.alumnimanager.repository.CollegeRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import jmu.lwk.alumnimanager.service.AuthService
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val collegeRepository: CollegeRepository,
    private val alumniAssociationRepository: AlumniAssociationRepository
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