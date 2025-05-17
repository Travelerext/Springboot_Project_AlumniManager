package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("activities")
data class Activity(
    @Id val id: ObjectId = ObjectId(),
    val name: String,
    val organizerId: ObjectId,
    val address: String,
    val date: Instant,
    val content: String,
    val limit: Int,
    val participantsList: List<ObjectId> = emptyList()
)
