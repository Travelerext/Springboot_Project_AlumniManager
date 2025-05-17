package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("Users")
data class User(
    @Id val id: ObjectId = ObjectId(),
    val name: String,
    val password: String,
    val role: Role? = null,
    val alumniId: ObjectId? = null,
    val manageId: ObjectId? = null
)

enum class Role{
    HeadMaster,
    CollegeAdmin,
    AssociationAdmin,
    GeneralAdmin
}
