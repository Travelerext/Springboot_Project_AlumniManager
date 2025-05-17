package jmu.lwk.alumnimanager.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("news")
data class News(
    @Id val id: ObjectId = ObjectId(),
    val title: String,
    val content: String,
    val author: String,
    val launchId: ObjectId,
    val writeDate: Instant = Instant.now()
)
