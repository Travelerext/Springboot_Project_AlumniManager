package jmu.lwk.alumnimanager.repository

import jmu.lwk.alumnimanager.model.AlumniAssociation
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface AlumniAssociationRepository: MongoRepository<AlumniAssociation, ObjectId> {

}