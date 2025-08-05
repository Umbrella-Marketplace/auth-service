package dev.zornov.market.auth.repository

import dev.zornov.market.auth.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String>