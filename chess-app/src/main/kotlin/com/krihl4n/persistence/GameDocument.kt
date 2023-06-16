package com.krihl4n.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "games")
data class GameDocument(@Id val id: String) {

}