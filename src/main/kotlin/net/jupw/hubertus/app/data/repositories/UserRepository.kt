package net.jupw.hubertus.app.data.repositories

import net.jupw.hubertus.app.data.entities.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserEntity, Int>