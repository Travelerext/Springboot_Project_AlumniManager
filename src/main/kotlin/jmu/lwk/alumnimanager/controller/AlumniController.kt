package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.Alumni
import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.model.Sex
import jmu.lwk.alumnimanager.repository.*
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/alumni")
class AlumniController(
    private val userRepository: UserRepository,
    private val alumniRepository: AlumniRepository,
    private val classRepository: ClassRepository,
    private val collegeRepository: CollegeRepository,
    private val specialityRepository: SpecialityRepository,
    private val alumniAssociationRepository: AlumniAssociationRepository,
    private val activityRepository: ActivityRepository
) {
    data class AlumniRequest(
        val studentId: String,
        val realName: String,
        val sex: String? = null,
        val birthday: Instant? = null,
        val collegeId: ObjectId? = null,
        val classId: ObjectId? = null,
        val specialityId: ObjectId? = null,
        val alumniAssociationId: ObjectId? = null,
        val admissionDate: Instant? = null,
        val graduationDate: Instant? = null,
        val industry: String? = null,
        val workPlace: String? = null,
        val phoneNumber: String? = null,
        val email: String? = null,
        val address: String? = null
    )

    data class AlumniResponse(
        val id: String,
        val studentId: String? = null,
        val realName: String? = null,
        val sex: String? = null,
        val birthday: Instant? = null,
        val collegeName: String? = null,
        val className: String? = null,
        val specialityName: String? = null,
        val alumniAssociationName: String? = null,
        val admissionDate: Instant? = null,
        val graduationDate: Instant? = null,
        val industry: String? = null,
        val workPlace: String? = null,
        val phoneNumber: String? = null,
        val email: String? = null,
        val address: String? = null,
        val starLevel: Int
    )

    @GetMapping
    fun getAlumni(): AlumniResponse {
        val id = SecurityContextHolder.getContext().authentication.principal as String
        val alumni = ObjectId(id).getAlumni()
        return alumni.toResponse()
    }

    @GetMapping("/classmate")
    fun getClassmates(): List<AlumniResponse> {
        val id = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("账号异常")
        }
        val alumni = user.alumniId?.let { alumniRepository.findById(it).orElseThrow{ IllegalArgumentException("资料获取失败") } }
            ?: throw IllegalArgumentException("资料为空")
        return alumni.classId?.let { alumniRepository.findByClassId(it).map { classmate -> classmate.toResponse() } } ?: return emptyList()
    }

    @GetMapping("/highstarlevel")
    fun getHighStarLevelAlumni(): List<AlumniResponse> {
        val id = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("账号异常")
        }
        return if (user.role == Role.HeadMaster) {
            alumniRepository.findByStarLevelBetween(4, 5).map { it.toResponse() }
        } else emptyList()
    }

    @GetMapping("/{id}")
    fun getAlumniById(@PathVariable id: String): AlumniResponse {
        val alumni = alumniRepository.findById(ObjectId(id)).orElseThrow { IllegalArgumentException("资料获取失败") }
        return alumni.toResponse()
    }

    @GetMapping("/real_name")
    fun getAlumniByRealName(@RequestParam realName: String): List<AlumniResponse> {
        val alumni = alumniRepository.findByRealNameContaining(realName)
        return alumni.map { it.toResponse() }
    }

    @PostMapping
    fun addAlumni(@RequestBody request: AlumniRequest): AlumniResponse {
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(operatorId)).orElseThrow { IllegalArgumentException("账号异常") }
        if (user.role != null) {
            return alumniRepository.save(
                Alumni(
                    realName = request.realName,
                    studentId = request.studentId,
                    sex = when (request.sex) {
                        "男" -> Sex.MALE
                        "女" -> Sex.FEMALE
                        else -> null
                    },
                    birthday = request.birthday,
                    collegeId = request.collegeId,
                    classId = request.classId,
                    specialityId = request.specialityId,
                    alumniAssociationId = request.alumniAssociationId,
                    admissionDate = request.admissionDate,
                    graduationDate = request.graduationDate,
                    industry = request.industry,
                    workPlace = request.workPlace,
                    phoneNumber = request.phoneNumber,
                    email = request.email,
                    address = request.address,
                )
            ).toResponse()
        } else throw IllegalArgumentException("权限不足")
    }

    @PutMapping
    fun updateAlumni(@RequestBody request: AlumniRequest): AlumniResponse {
        val id = SecurityContextHolder.getContext().authentication.principal as String
        val alumni = ObjectId(id).getAlumni()
        return alumniRepository.save(
            alumni.copy(
                sex = when(request.sex) {
                    "男" -> Sex.MALE
                    "女" -> Sex.FEMALE
                    else -> alumni.sex
                },
                birthday = request.birthday ?: alumni.birthday,
                collegeId = request.collegeId ?: alumni.collegeId,
                classId = request.classId ?: alumni.classId,
                specialityId = request.specialityId ?: alumni.specialityId,
                alumniAssociationId = request.alumniAssociationId,
                admissionDate = request.admissionDate ?: alumni.admissionDate,
                graduationDate = request.graduationDate ?: alumni.graduationDate,
                industry = request.industry ?: alumni.industry,
                workPlace = request.workPlace ?: alumni.workPlace,
                phoneNumber = request.phoneNumber ?: alumni.phoneNumber,
                email = request.email ?: alumni.email,
                address = request.address ?: alumni.address,
            )
        ).toResponse()
    }

    @PutMapping(path = ["/{id}"])
    fun updateAlumniById(@PathVariable id: String, @RequestBody request: AlumniRequest): AlumniResponse {
        val alumni = alumniRepository.findById(ObjectId(id)).orElseThrow { IllegalArgumentException("校友不存在") }
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        if (ObjectId(operatorId).beAuthorized(alumni)) {
            return alumniRepository.save(
                alumni.copy(
                    studentId = request.studentId,
                    realName = request.realName,
                    sex = when(request.sex) {
                        "男" -> Sex.MALE
                        "女" -> Sex.FEMALE
                        else -> alumni.sex
                    },
                    birthday = request.birthday ?: alumni.birthday,
                    collegeId = request.collegeId ?: alumni.collegeId,
                    classId = request.classId ?: alumni.classId,
                    specialityId = request.specialityId ?: alumni.specialityId,
                    alumniAssociationId = request.alumniAssociationId,
                    admissionDate = request.admissionDate ?: alumni.admissionDate,
                    graduationDate = request.graduationDate ?: alumni.graduationDate,
                    industry = request.industry ?: alumni.industry,
                    workPlace = request.workPlace ?: alumni.workPlace,
                    phoneNumber = request.phoneNumber ?: alumni.phoneNumber,
                    email = request.email ?: alumni.email,
                    address = request.address ?: alumni.address,
                )
            ).toResponse()
        } else throw IllegalArgumentException("权限不足")
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteAlumni(@PathVariable id: String) {
        val alumni = alumniRepository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("校友不存在")
        }
        val operatorId = SecurityContextHolder.getContext().authentication.principal as String
        if (ObjectId(operatorId).beAuthorized(alumni))
            alumniRepository.delete(alumni)
    }

    private fun ObjectId.getAlumni(): Alumni {
        val user = userRepository.findById(this).orElseThrow { IllegalArgumentException("资料获取失败") }
        return user.alumniId?.let { alumniRepository.findById(it).orElseThrow{ IllegalArgumentException("资料获取失败") } }
            ?: throw IllegalArgumentException("资料为空")
    }

    private fun Alumni.toResponse(): AlumniResponse {
        val collegeName = collegeId?.let { collegeRepository.findById(it).orElse(null)?.name }
        val classNumber = classId?.let { classRepository.findById(it).orElse(null)?.classNumber }
        val classYears = classId?.let { classRepository.findById(it).orElse(null)?.years }
        val specialityName = specialityId?.let { specialityRepository.findById(it).orElse(null)?.name }
        val alumniAssociationName = alumniAssociationId?.let { alumniAssociationRepository.findById(it).orElse(null)?.name }
        return AlumniResponse(
            id = id.toHexString(),
            studentId = studentId,
            realName = realName,
            sex = sex?.name,
            birthday = birthday,
            collegeName = collegeName,
            specialityName = specialityName,
            className = if (classYears != null && collegeName != null && specialityName != null && classNumber != null)
                "$classYears $collegeName $specialityName $classNumber"
            else null,
            alumniAssociationName = alumniAssociationName,
            admissionDate = admissionDate,
            graduationDate = graduationDate,
            industry = industry,
            workPlace = workPlace,
            phoneNumber = phoneNumber,
            email = email,
            address = address,
            starLevel = starLevel
        )
    }

    private fun ObjectId.beAuthorized(alumni: Alumni): Boolean {
        val operator = userRepository.findById(this).orElseThrow{
            IllegalArgumentException("账号异常")
        }
        return operator.role == Role.GeneralAdmin ||
        (operator.role == Role.CollegeAdmin && operator.manageId == alumni.collegeId) ||
        (operator.role == Role.AssociationAdmin && operator.manageId == alumni.alumniAssociationId)
    }
}