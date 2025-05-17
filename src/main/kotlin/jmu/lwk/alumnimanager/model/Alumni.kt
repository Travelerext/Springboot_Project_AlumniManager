package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("alumni")
data class Alumni(
    @Id val id: ObjectId = ObjectId(),
    val studentId: String? = null,
    val realName: String? = null,
    val sex: Sex? = null,
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
    val starLevel: Int = 1,
    val address: String? = null
)

enum class Sex{
    MALE,
    FEMALE
}