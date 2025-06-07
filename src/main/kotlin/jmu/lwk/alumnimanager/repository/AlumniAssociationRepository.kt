package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.AlumniAssociation
import jmu.lwk.alumnimanager.model.College
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface AlumniAssociationRepository: MongoRepository<AlumniAssociation, ObjectId> {
    @Query("{ 'name': { \$regex: ?0, \$options: 'i' } }")
    fun findByNameContaining(name: String): List<AlumniAssociation>
}