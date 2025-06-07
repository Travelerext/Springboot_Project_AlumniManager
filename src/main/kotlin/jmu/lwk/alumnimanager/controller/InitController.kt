package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.Alumni
import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.model.UnconfirmedAlumni
import jmu.lwk.alumnimanager.repository.AlumniRepository
import jmu.lwk.alumnimanager.repository.UnconfirmedRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/init")
class InitController(
    private val userRepository: UserRepository,
    private val unconfirmedRepository: UnconfirmedRepository,
    private val alumniRepository: AlumniRepository,
) {
    data class InitAlumniRequest(
        val realName: String,
        val studentId: String
    )

    data class UnconfirmedAlumniResponse(
        val id: String,
        val realName: String,
        val studentId: String
    )

    data class ApiResponse(
        val success: Boolean,
        val message: String
    )

    @PostMapping
    fun initAlumniInfo(@RequestBody request: InitAlumniRequest): ApiResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow{
            IllegalArgumentException("账号异常")
        }
        val alumni = alumniRepository.findByStudentId(request.studentId)
        return if (alumni != null) {
            if (alumni.realName != request.realName) {
                return ApiResponse(false, "姓名不匹配")
            } else if (userRepository.findByAlumniId(alumni.id) != null) {
                return ApiResponse(false, "该校友已绑定账号")
            } else {
                userRepository.save(user.copy(alumniId = alumni.id))
                return ApiResponse(true, "校友信息绑定成功")
            }
        } else {
            unconfirmedRepository.save(UnconfirmedAlumni(realName = request.realName, studentId = request.studentId))
            return ApiResponse(false, "校友信息待审核")
        }
    }

    @PostMapping("/verify")
    @Transactional
    fun verifyAlumni(@RequestParam id: String, @RequestParam approved: Boolean): ApiResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow{ IllegalArgumentException("账号异常") }
        if (user.role != Role.GeneralAdmin) return ApiResponse(false, "权限不足")
        val unconfirmedAlumni = unconfirmedRepository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("审核信息异常")
        }
        return if (approved) {
            alumniRepository.save(
                Alumni(
                    studentId = unconfirmedAlumni.studentId,
                    realName = unconfirmedAlumni.realName
                )
            )
            unconfirmedRepository.delete(unconfirmedAlumni)
            ApiResponse(true, "校友审核通过，信息已录入")
        } else {
            unconfirmedRepository.delete(unconfirmedAlumni)
            ApiResponse(false, "校友审核未通过")
        }
    }

    @GetMapping
    fun getUnconfirmedAlumni(): List<UnconfirmedAlumniResponse> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow{ IllegalArgumentException("账号异常") }
        if (user.role != Role.GeneralAdmin) throw IllegalArgumentException("权限不足")
        return unconfirmedRepository.findAll().map {
            UnconfirmedAlumniResponse(
                id = it.id.toHexString(),
                realName = it.realName,
                studentId = it.studentId
            )
        }
    }

}