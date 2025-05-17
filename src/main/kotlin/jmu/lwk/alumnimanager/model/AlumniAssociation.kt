package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("alumni_association")
data class AlumniAssociation(
    @Id val id: ObjectId = ObjectId(),
    val name: String,
    val location: String,
    val superintendentName: String,
    val superintendentPhoneNumber: String
)
