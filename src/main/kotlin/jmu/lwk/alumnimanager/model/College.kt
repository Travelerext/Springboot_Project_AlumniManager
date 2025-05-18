package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("colleges")
data class College(
    @Id val id: ObjectId = ObjectId(),
    val oldName: String? = null,
    val name: String,
    val years: Int
)
