package net.jupw.hubertus.app.data.repositories

import net.jupw.hubertus.app.data.entities.ActivityEntity
import org.springframework.data.repository.CrudRepository

interface ActivityRepository : CrudRepository<ActivityEntity, Int>