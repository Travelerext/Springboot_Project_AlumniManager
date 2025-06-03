package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.Activity
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ActivityRepository: MongoRepository<Activity, ObjectId> {
    @Query("{ 'name': { \$regex: ?0, \$options: 'i' } }")
    fun findAllByNameContaining(name: String): List<Activity>
    fun findAllByOrganizerId(organizerId: ObjectId): List<Activity>
    fun findByParticipantsListContains(alumniId: ObjectId): List<Activity>
    fun findAllByOrderByDateDesc(pageable: Pageable): List<Activity>
    fun findAllByOrderByParticipantsListDesc(pageable: Pageable): List<Activity>
}