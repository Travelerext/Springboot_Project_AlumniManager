package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.College
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface CollegeRepository: MongoRepository<College, ObjectId> {
    @Query("{ 'name': { \$regex: ?0, \$options: 'i' } }")
    fun findByNameContaining(name: String): List<College>
}