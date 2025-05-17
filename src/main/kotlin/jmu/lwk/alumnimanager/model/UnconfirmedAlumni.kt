package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("unconfirmed")
data class UnconfirmedAlumni(
    @Id val id: ObjectId = ObjectId(),
    val realName: String,
    val studentId: String
)
