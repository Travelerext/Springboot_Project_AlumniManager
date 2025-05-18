package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Class
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ClassRepository: MongoRepository<Class, ObjectId> {
    fun findByClassNumberAndCollegeIdAndSpecialityIdAndYearsContaining(classNumber: Int?, collegeId: ObjectId?, specialityId: ObjectId?, years: Int?): List<Class>
}