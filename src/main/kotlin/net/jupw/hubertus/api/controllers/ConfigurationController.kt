package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.models.ConfigurationEntrySchema
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.configuration.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

typealias ConfigurationGroupKeyModel = net.jupw.hubertus.api.models.ConfigurationGroupKey
typealias ConfigurationKeyModel = net.jupw.hubertus.api.models.ConfigurationKey
typealias ConfigurationEntryModel = net.jupw.hubertus.api.models.ConfigurationEntry
typealias ConfigurationEntrySchemaModel = net.jupw.hubertus.api.models.ConfigurationEntrySchema

@RestController
class ConfigurationController : ConfigurationApi {


    @Autowired
    private lateinit var configuration: Configuration

    override fun configurationGet(): ResponseEntity<MutableList<ConfigurationGroupKeyModel>> =
        ConfGroupKeys.values().map { it.toModel() }.toMutableList().toResponseEntity()

    override fun configurationGroupKeyGet(groupKey: String): ResponseEntity<MutableList<ConfigurationEntrySchema>>? =
        when(ConfGroupKeys.valueOfOrNull(groupKey)) {
            null -> mutableListOf<ConfigurationEntrySchema>().toResponseEntity()
            ConfGroupKeys.DEFAULT -> ConfKeys.values()
                .map { keySchema(it, configuration[it]) }
                .toMutableList()
                .toResponseEntity()

        }

    override fun configurationGroupKeyKeyDelete(groupKey: String?, key: String?): ResponseEntity<Void> {
        return super.configurationGroupKeyKeyDelete(groupKey, key)
    }

    override fun configurationGroupKeyKeyGet(groupKey: String?, key: String?): ResponseEntity<ConfigurationEntryModel> {
        return super.configurationGroupKeyKeyGet(groupKey, key)
    }

    override fun configurationGroupKeyKeyPost(
        groupKey: String?,
        key: String?,
        body: ConfigurationEntryModel?
    ): ResponseEntity<Void> {
        return super.configurationGroupKeyKeyPost(groupKey, key, body)
    }

    override fun configurationGroupKeyPost(groupKey: String?, body: ConfigurationEntryModel?): ResponseEntity<Void> {
        return super.configurationGroupKeyPost(groupKey, body)
    }

    fun ConfigurationGroupKey.toModel() = ConfigurationGroupKeyModel().also {
        it.key = key
        it.name = displayName
        it.description = description
    }

    fun ConfigurationEntry.toModel() = ConfigurationEntryModel().also {
        it.key = key.key
        it.value = value
    }

    fun ConfigurationKey.toModel() = ConfigurationKeyModel().also {
        it.key = key
        it.name = displayName
        it.description = description
        it.defaultValue = defaultValue
    }

    fun keySchema(key: ConfigurationKey, value: String) = ConfigurationEntrySchemaModel().also {
        it.key = key.toModel()
        it.value = value
    }
}