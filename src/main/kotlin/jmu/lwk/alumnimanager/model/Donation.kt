package jmu.lwk.alumnimanager.model

import org.apache.logging.log4j.message.Message
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("donation")
data class Donation(
    @Id val id: ObjectId = ObjectId(),
    val amount: Long? = null,
    val items: String?= null,
    val donationDate: Instant,
    val alumniId: ObjectId,
    val message: String = "",
    val isChecked: Boolean = false
)
