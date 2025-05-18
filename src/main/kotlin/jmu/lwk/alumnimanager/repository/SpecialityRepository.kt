package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Speciality
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface SpecialityRepository: MongoRepository<Speciality, ObjectId> {
    @Query("{ 'name': { \$regex: ?0, \$options: 'i' } }")
    fun findByName(name: String): List<Speciality>
}