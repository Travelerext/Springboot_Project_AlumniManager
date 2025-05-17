package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Alumni
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface AlumniRepository: MongoRepository<Alumni, ObjectId> {

    @Query("{ 'realName': { \$regex: ?0, \$options: 'i' } }")
    fun findByRealNameContaining(name: String): List<Alumni>
    fun findByStudentId(studentId: String): Alumni?
    fun findByClassId(classId: ObjectId): List<Alumni>
    fun findByStarLevelBetween(lowerBound: Int, upperBound: Int): List<Alumni>
}