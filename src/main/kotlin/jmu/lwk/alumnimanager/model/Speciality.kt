package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("specialities")
data class Speciality(
    @Id val id: ObjectId = ObjectId(),
    val collegeId: ObjectId,
    val oldCollegeId: ObjectId? = null,
    val name: String,
    val oldName: String? = null,
    val classCount: Int = 0,
    val years: Int
)
