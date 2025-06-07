package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.model.User
import jmu.lwk.alumnimanager.repository.AlumniAssociationRepository
import jmu.lwk.alumnimanager.repository.CollegeRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/user")
class UserController(
    private val userRepository: UserRepository,
    private val collegeRepository: CollegeRepository,
    private val alumniAssociationRepository: AlumniAssociationRepository
) {

    data class UserRequest(
        val id: String,
        val role: String,
        val manageId: String?
    )

    data class UserResponse(
        val id: String,
        val name: String,
        val alumniId: String?,
        val roleInfo: String?
    )

    @PostMapping("/empower")
    fun empower(@RequestBody userRequest: UserRequest): UserResponse {
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        val operator = userRepository.findById(ObjectId(operatorId)).orElseThrow {
            IllegalArgumentException("管理员不存在")
        }
        if (operator.role == Role.GeneralAdmin) {
            val user = userRepository.findById(ObjectId(userRequest.id)).orElseThrow{ IllegalArgumentException("用户不存在") }
            user?.let {
                when (userRequest.role) {
                    "学院负责人" -> {
                        if (userRequest.manageId != null && collegeRepository.findById(ObjectId(userRequest.manageId)).getOrNull() != null) {
                            return userRepository.save(
                                user.copy(
                                    role = Role.CollegeAdmin,
                                    manageId = ObjectId(userRequest.manageId)
                                )
                            ).toResponse()
                        } else throw IllegalArgumentException("授权失败")
                    }
                    "校友分会负责人" -> {
                        if (userRequest.manageId != null && alumniAssociationRepository.findById(ObjectId(userRequest.manageId)).getOrNull() != null) {
                            return userRepository.save(
                                user.copy(
                                    role = Role.AssociationAdmin,
                                    manageId = ObjectId(userRequest.manageId)
                                )
                            ).toResponse()
                        } else throw IllegalArgumentException("授权失败")
                    }
                    "校领导" -> {
                        return userRepository.save(
                            user.copy(
                                role = Role.HeadMaster,
                                manageId = ObjectId(userRequest.id)
                            )
                        ).toResponse()
                    }
                    "校友总会工作人员" -> {
                        return userRepository.save(
                            user.copy(
                                role = Role.GeneralAdmin,
                                manageId = ObjectId(userRequest.id)
                            )
                        ).toResponse()
                    }
                    else -> throw IllegalArgumentException("授权失败")
                }
            } ?: throw IllegalArgumentException("账号异常")
        } else throw IllegalArgumentException("无权限进行授权")
    }

    @GetMapping("/role")
    fun getRole(): String? {
        val id = SecurityContextHolder.getContext().authentication.principal as String
        return userRepository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("账号异常")
        }.role?.name
    }

    @GetMapping
    fun getUserInfo(): UserResponse {
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        val operator = userRepository.findById(ObjectId(operatorId)).orElseThrow { IllegalArgumentException("账号异常") }
        return operator.toResponse()
    }

    @GetMapping("founder")
    fun findUser(@RequestParam name: String): List<UserResponse> {
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        val operator = userRepository.findById(ObjectId(operatorId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (operator.role == Role.GeneralAdmin)
            return userRepository.findByNameContaining(name).map { it.toResponse() }
        else throw IllegalArgumentException("权限不足")
    }

    @GetMapping("/{id}")
    fun getUserInfoById(@PathVariable id: String): UserResponse {
        val user = userRepository.findById(ObjectId(id)).orElseThrow{ IllegalArgumentException("用户不存在") }
        return user.toResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteUserById(@PathVariable id: String) {
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        val operator = userRepository.findById(ObjectId(operatorId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (operator.role == Role.GeneralAdmin)
            userRepository.deleteById(ObjectId(id))
        else throw IllegalArgumentException("权限不足")
    }

    private fun User.toResponse(): UserResponse {
        val roleInfo = when (role) {
            Role.HeadMaster -> "校领导"
            Role.GeneralAdmin -> "校友总会工作人员"
            Role.CollegeAdmin -> {
                val collegeName = manageId?.let { collegeRepository.findById(it).getOrNull()?.name }
                "${collegeName}校友工作负责人"
            }
            Role.AssociationAdmin -> {
                val associationName = manageId?.let { alumniAssociationRepository.findById(it).getOrNull()?.name }
                "${associationName}负责人"
            }
            else -> null
        }
        return UserResponse(
            id = id.toHexString(),
            name = name,
            alumniId = alumniId?.toHexString(),
            roleInfo = roleInfo
        )
    }
}