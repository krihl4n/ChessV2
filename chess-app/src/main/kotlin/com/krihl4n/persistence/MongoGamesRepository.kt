package com.krihl4n.persistence

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoGamesRepository: MongoRepository<GameDocument, String>