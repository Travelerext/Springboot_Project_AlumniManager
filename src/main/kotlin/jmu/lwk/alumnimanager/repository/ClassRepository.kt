package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Class
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ClassRepository: MongoRepository<Class, ObjectId> {
    fun findByCollegeIdAndSpecialityId(collegeId: ObjectId?, specialityId: ObjectId?): List<Class>
}