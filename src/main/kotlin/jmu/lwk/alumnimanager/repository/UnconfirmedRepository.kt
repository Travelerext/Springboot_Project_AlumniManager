package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.UnconfirmedAlumni
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UnconfirmedRepository: MongoRepository<UnconfirmedAlumni, ObjectId> {

}