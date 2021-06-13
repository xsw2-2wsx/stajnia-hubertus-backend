package net.jupw.hubertus.app.data.repositories

import net.jupw.hubertus.app.data.entities.ConfigurationEntryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ConfigurationRepository : CrudRepository<ConfigurationEntryEntity, Int> {

    fun findByGroup(group: String): Iterable<ConfigurationEntryEntity>

    fun deleteByKeyAndGroup(key: String, group: String)

    fun findByGroupAndKey(group: String, key: String): ConfigurationEntryEntity?

}