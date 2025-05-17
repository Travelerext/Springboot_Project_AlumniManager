package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.News
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface NewsRepository: MongoRepository<News, ObjectId> {

    @Query("{ 'title': { \$regex: ?0, \$options: 'i' } }")
    fun findAllByTitleContaining(title: String): List<News>
    fun findAllByOrderByWriteDateDesc(pageable: Pageable): List<News>
}