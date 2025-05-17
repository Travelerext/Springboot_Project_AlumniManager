package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Donation
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface DonationRepository: MongoRepository<Donation, ObjectId> {
    fun findByAlumniIdAndItemsIsNotNull(alumniId: ObjectId): List<Donation>
    fun findByAlumniIdAndAmountIsNotNull(alumniId: ObjectId): List<Donation>
    fun findByCheckedFalse(): List<Donation>
}