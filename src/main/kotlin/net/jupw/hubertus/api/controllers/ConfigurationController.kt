package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.ConfigurationGroupKeyModel
import net.jupw.hubertus.api.converters.keySchema
import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.ConfigurationEntrySchema
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.configuration.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController



@RestController
class ConfigurationController : ConfigurationApi {


    @Autowired
    private lateinit var configuration: Configuration

    override fun configurationGet(): ResponseEntity<List<ConfigurationGroupKeyModel>> =
        ConfGroupKeys.values().map { it.toModel() }.toMutableList().toResponseEntity()

    override fun configurationGroupKeyGet(groupKey: String): ResponseEntity<List<ConfigurationEntrySchema>> =
        when(ConfGroupKeys.valueOfOrNull(groupKey)) {
            null -> mutableListOf<ConfigurationEntrySchema>().toResponseEntity()
            ConfGroupKeys.DEFAULT -> ConfKeys.values()
                .map { keySchema(it, configuration[it]) }
                .toMutableList()
                .toResponseEntity()

        }

    override fun configurationGroupKeyKeyDelete(groupKey: String, key: String): ResponseEntity<Unit> {
        return super.configurationGroupKeyKeyDelete(groupKey, key)
    }

    override fun configurationGroupKeyKeyGet(
        groupKey: String,
        key: String
    ): ResponseEntity<net.jupw.hubertus.api.models.ConfigurationEntry> {
        return super.configurationGroupKeyKeyGet(groupKey, key)
    }

    override fun configurationGroupKeyKeyPost(
        groupKey: String,
        key: String,
        configurationEntry: net.jupw.hubertus.api.models.ConfigurationEntry
    ): ResponseEntity<Unit> {
        return super.configurationGroupKeyKeyPost(groupKey, key, configurationEntry)
    }

    override fun configurationGroupKeyPost(
        groupKey: String,
        configurationEntry: net.jupw.hubertus.api.models.ConfigurationEntry
    ): ResponseEntity<Unit> {
        return super.configurationGroupKeyPost(groupKey, configurationEntry)
    }

}