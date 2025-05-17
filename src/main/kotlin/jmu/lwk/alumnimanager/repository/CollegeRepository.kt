package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.College
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface CollegeRepository: MongoRepository<College, ObjectId> {

    fun findByName(name: String): College
}