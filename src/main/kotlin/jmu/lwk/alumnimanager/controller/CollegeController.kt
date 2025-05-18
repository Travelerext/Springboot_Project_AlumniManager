package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.College
import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.repository.CollegeRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/college")
class CollegeController (
    private val collegeRepository: CollegeRepository,
    private val userRepository: UserRepository
) {

    data class CollegeRequest(
        val oldName: String? = null,
        val name: String,
        val years: Int
    )

    data class CollegeResponse(
        val id: String,
        val oldName: String? = null,
        val name: String,
        val years: Int
    )

    @GetMapping
    fun findCollege(@RequestParam name: String): List<CollegeResponse> {
        return collegeRepository.findByNameContaining(name).map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getCollegeById(@PathVariable id: String): CollegeResponse {
        return collegeRepository.findById(ObjectId(id)).orElseThrow{ IllegalArgumentException("学院不存在") }
            .toResponse()
    }

    @PostMapping
    fun createCollege(@RequestBody request: CollegeRequest): CollegeResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != Role.GeneralAdmin) throw IllegalArgumentException("权限不足")
        return collegeRepository.save(
            College(
                oldName = request.oldName,
                name = request.name,
                years = request.years
            )
        ).toResponse()
    }

    @PutMapping("/{id}")
    fun updateCollege(
        @PathVariable id: String,
        @RequestBody request: CollegeRequest
    ): CollegeResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != Role.GeneralAdmin) throw IllegalArgumentException("权限不足")
        val college = collegeRepository.findById(ObjectId(id))
            .orElseThrow { IllegalArgumentException("学院不存在") }

        return collegeRepository.save(
            college.copy(
                oldName = request.oldName ?: college.oldName,
                name = request.name,
                years = request.years
            )
        ).toResponse()
    }



    private fun College.toResponse(): CollegeResponse {
        return CollegeResponse(
            id = id.toHexString(),
            oldName = oldName,
            name = name,
            years = years
        )
    }

}