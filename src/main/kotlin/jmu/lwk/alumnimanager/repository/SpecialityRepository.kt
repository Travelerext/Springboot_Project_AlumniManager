package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Speciality
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface SpecialityRepository: MongoRepository<Speciality, ObjectId> {
    fun findByName(name: String): Speciality?
}