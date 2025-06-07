package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.controller.ClassController.ClassRequest
import jmu.lwk.alumnimanager.controller.ClassController.ClassResponse
import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.model.Speciality
import jmu.lwk.alumnimanager.repository.SpecialityRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/speciality")
class SpecialityController(
    private val specialityRepository: SpecialityRepository,
    private val userRepository: UserRepository,
) {

    data class SpecialityRequest(
        val collegeId: String,
        val oldCollegeId: String? = null,
        val name: String,
        val oldName: String? = null,
        val years: Int
    )

    data class SpecialityResponse(
        val id: String,
        val collegeId: String,
        val oldCollegeId: String? = null,
        val name: String,
        val oldName: String? = null,
        val years: Int
    )

    @GetMapping("/all")
    fun getAllSpecialities(): List<SpecialityResponse> {
        return specialityRepository.findAll().map { it.toResponse() }
    }

    @GetMapping
    fun findSpeciality(@RequestParam collegeId: String): List<SpecialityResponse> {
        return specialityRepository.findByCollegeId(ObjectId(collegeId))
            .map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getSpeciality(@PathVariable id: ObjectId): SpecialityResponse {
        return specialityRepository.findById(id).orElseThrow { IllegalArgumentException("专业不存在") }.toResponse()
    }

    @GetMapping("/founder")
    fun findSpecialityByName(@RequestParam name: String): List<SpecialityResponse> {
        return specialityRepository.findByNameContaining(name)
            .map { it.toResponse() }
    }

    @PostMapping
    fun addSpeciality(@RequestBody request: SpecialityRequest): SpecialityResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != Role.GeneralAdmin && (user.role != Role.CollegeAdmin || user.manageId != ObjectId(request.collegeId) )) throw IllegalArgumentException("权限不足")
        println("collegeId: $request.collegeId")
        val speciality = Speciality(
            collegeId = ObjectId(request.collegeId),
            oldCollegeId = request.oldCollegeId.takeIf { it?.isNotEmpty()?: false }?.let { ObjectId(it) },
            name = request.name,
            oldName = request.oldName,
            years = request.years
        )
        return specialityRepository.save(speciality).toResponse()
    }

    @PutMapping("/{id}")
    fun updateSpeciality(
        @PathVariable id: String,
        @RequestBody request: SpecialityRequest
    ): SpecialityResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != Role.GeneralAdmin && (user.role != Role.CollegeAdmin || user.manageId != ObjectId(request.collegeId) )) throw IllegalArgumentException("权限不足")
        val existing = specialityRepository.findById(ObjectId(id))
            .orElseThrow { IllegalArgumentException("专业不存在") }

        val updatedSpeciality = existing.copy(
            collegeId = ObjectId(request.collegeId),
            oldCollegeId = request.oldCollegeId?.let { ObjectId(it) } ?: existing.oldCollegeId,
            name = request.name,
            oldName = request.oldName ?: existing.oldName,
            years = request.years
        )
        return specialityRepository.save(updatedSpeciality).toResponse()
    }

    private fun Speciality.toResponse(): SpecialityResponse {
        return SpecialityResponse(
            id = id.toHexString(),
            collegeId = collegeId.toHexString(),
            oldCollegeId = oldCollegeId?.toHexString(),
            name = name,
            oldName = oldName,
            years = years
        )
    }
}