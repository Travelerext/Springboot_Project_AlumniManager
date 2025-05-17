package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Class
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ClassRepository: MongoRepository<Class, ObjectId> {
    fun findByClassNumberAndCollegeIdAndYearsContaining(classNumber: Int?, collegeId: ObjectId?, years: Int?): List<Class>
}