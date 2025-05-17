package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Alumni
import jmu.lwk.alumnimanager.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface UserRepository: MongoRepository<User, ObjectId> {

    @Query(" { 'name': { \$regex: ?0, \$options: 'i' } } ")
    fun findByNameContaining(name: String): List<User>
    fun findByName(name: String): User?
    fun findByAlumniId(alumniId: ObjectId): User?
}