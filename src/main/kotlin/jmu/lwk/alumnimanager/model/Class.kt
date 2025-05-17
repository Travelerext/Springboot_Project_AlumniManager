package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("classes")
data class Class(
    @Id val id: ObjectId = ObjectId(),
    val classNumber: Int,
    val collegeId: ObjectId,
    val oldCollegeId: ObjectId? = null,
    val specialityId: ObjectId,
    val oldSpecialId: ObjectId? = null,
    val years: Int
)
