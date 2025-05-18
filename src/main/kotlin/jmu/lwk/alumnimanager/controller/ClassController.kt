package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.Class
import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.repository.ClassRepository
import jmu.lwk.alumnimanager.repository.CollegeRepository
import jmu.lwk.alumnimanager.repository.SpecialityRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/class")
class ClassController(
    private val classRepository: ClassRepository,
    private val collegeRepository: CollegeRepository,
    private val specialityRepository: SpecialityRepository,
    private val userRepository: UserRepository
) {

    data class ClassRequest(
        val collegeId: String?,
        val specialityId: String?,
        val classNumber: Int?,
        val years : Int?
    )

    data class ClassResponse(
        val classId: String,
        val collegeName: String,
        val specialityName: String,
        val classNumber: Int,
        val years: Int
    )

    @GetMapping
    fun findClasses(@RequestBody request: ClassRequest): List<ClassResponse> {
        return classRepository.findByClassNumberAndCollegeIdAndSpecialityIdAndYearsContaining(
            request.classNumber,
            ObjectId(request.collegeId),
            ObjectId(request.specialityId),
            request.years
        ).map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getClassById(@PathVariable id: ObjectId): ClassResponse {
        return classRepository.findById(id).orElseThrow{ IllegalStateException("班级不存在") }
            .toResponse()
    }

    @PostMapping
    fun createClass(@RequestBody request: ClassRequest): ClassResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != Role.GeneralAdmin && (user.role != Role.CollegeAdmin || user.manageId != ObjectId(request.collegeId) )) throw IllegalArgumentException("权限不足")
        val collegeId = request.collegeId ?: throw IllegalArgumentException("collegeId 不能为空")
        val specialityId = request.specialityId ?: throw IllegalArgumentException("specialityId 不能为空")
        val classNumber = request.classNumber ?: throw IllegalArgumentException("classNumber 不能为空")
        val years = request.years ?: throw IllegalArgumentException("years 不能为空")

        val newClass = Class(
            collegeId = ObjectId(collegeId),
            specialityId = ObjectId(specialityId),
            classNumber = classNumber,
            years = years
        )

        return classRepository.save(newClass).toResponse()
    }




    @PutMapping("/{id}")
    fun updateClass(@PathVariable id: String, @RequestBody request: ClassRequest): ClassResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != Role.GeneralAdmin && (user.role != Role.CollegeAdmin || user.manageId != ObjectId(request.collegeId) )) throw IllegalArgumentException("权限不足")
        val existingClass = classRepository.findById(ObjectId(id))
            .orElseThrow { IllegalStateException("班级不存在") }
        val updatedClass = existingClass.copy(
            collegeId = request.collegeId?.let { ObjectId(it) } ?: existingClass.collegeId,
            specialityId = request.specialityId?.let { ObjectId(it) } ?: existingClass.specialityId,
            classNumber = request.classNumber ?: existingClass.classNumber,
            years = request.years ?: existingClass.years
        )
        return classRepository.save(updatedClass).toResponse()
    }



    private fun Class.toResponse(): ClassResponse {
        return ClassResponse(
            classId = id.toHexString(),
            collegeName = collegeRepository.findById(collegeId).getOrNull()?.name ?: throw IllegalArgumentException("学院信息错误"),
            specialityName = specialityRepository.findById(specialityId).getOrNull()?.name ?: throw IllegalArgumentException("学院信息错误"),
            classNumber = classNumber,
            years = years
        )
    }
}