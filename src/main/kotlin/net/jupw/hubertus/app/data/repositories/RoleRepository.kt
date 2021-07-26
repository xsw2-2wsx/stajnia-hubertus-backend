package net.jupw.hubertus.app.data.repositories

import net.jupw.hubertus.app.data.entities.RoleEntity
import org.springframework.data.repository.CrudRepository

interface RoleRepository : CrudRepository<RoleEntity, Int>