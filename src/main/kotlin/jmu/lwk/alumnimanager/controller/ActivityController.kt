package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.Activity
import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.repository.ActivityRepository
import jmu.lwk.alumnimanager.repository.AlumniRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant

@Controller
@RequestMapping("/activity")
class ActivityController(
    val activityRepository: ActivityRepository,
    val alumniRepository: AlumniRepository,
    private val userRepository: UserRepository
) {

    data class ActivityRequest(
        val name: String,
        val address: String,
        val date: Instant,
        val content: String,
        val limit: Int
    )

    data class ActivityResponse(
        val activityId: String,
        val activityName: String,
        val organizerName: String,
        val address: String,
        val date: Instant,
        val content: String,
        val limit: Int,
        val participants: List<ObjectId>
    )

    @GetMapping("/sort_date")
    fun getAllActivitiesSortByDate(@RequestParam loadedPageSize: Int): List<ActivityResponse> {
        return activityRepository.findAllByOrderByDateDesc(PageRequest.of(loadedPageSize, 10)).map { it.toResponse() }
    }

    @GetMapping("/sort_participants")
    fun getAllActivitiesSortByParticipants(@RequestParam loadedPageSize: Int): List<ActivityResponse> {
        return activityRepository.findAllByOrderByParticipantsListDesc(PageRequest.of(loadedPageSize, 10)).map { it.toResponse() }
    }

    @GetMapping("/participants")
    fun getParticipants(@RequestParam id: String): List<String> {
        val activity = activityRepository.findById(ObjectId(id)).orElseThrow { IllegalArgumentException("活动不存在") }
        return activity.participantsList.map { it.toHexString() }
    }

    @PostMapping
    fun addActivity(@RequestBody activity: ActivityRequest): ActivityResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != null)
        {
            return activityRepository.save(
                Activity(
                    name = activity.name,
                    address = activity.address,
                    date = activity.date,
                    content = activity.content,
                    limit = activity.limit,
                    organizerId = ObjectId(userId),
                )
            ).toResponse()
        } else throw IllegalArgumentException("权限不足")
    }

    @PostMapping(path = ["/{id}"])
    fun updateActivity(@PathVariable id: String, @RequestBody activity: ActivityRequest): ActivityResponse {
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        val activity = activityRepository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("活动不存在")
        }
        if (ObjectId(operatorId) != activity.organizerId) throw IllegalArgumentException("无权修改")
        return activityRepository.save(
            activity.copy(
                name = activity.name,
                address = activity.address,
                date = activity.date,
                content = activity.content,
                limit = activity.limit
            )
        ).toResponse()
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteActivity(@PathVariable id: String) {
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        val operator = userRepository.findById(ObjectId(operatorId)).orElseThrow { IllegalArgumentException("账号异常") }
        val activity = activityRepository.findById(ObjectId(id)).orElseThrow { IllegalArgumentException("活动不存在") }
        val organizer = userRepository.findById(activity.organizerId).orElseThrow { IllegalArgumentException("组织者不存在") }
        if (
            operator.role == Role.GeneralAdmin ||
            operator.manageId == organizer.manageId
        ) {
            activityRepository.deleteById(ObjectId(operatorId))
        } else throw IllegalArgumentException("无权删除")
    }

    private fun Activity.toResponse(): ActivityResponse {
        val organizerName = alumniRepository.findById(organizerId).orElseThrow {
            IllegalArgumentException("账号异常")
        }.realName
        return ActivityResponse(
            activityId = id.toHexString(),
            activityName = name,
            organizerName = organizerName!!,
            address = address,
            date = date,
            content = content,
            limit = limit,
            participants = participantsList
        )
    }
}