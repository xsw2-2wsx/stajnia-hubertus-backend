package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.ConfigurationGroupKeyModel
import net.jupw.hubertus.api.converters.keySchema
import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.ConfigurationEntrySchema
import net.jupw.hubertus.api.models.ConfigurationValue
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
        ConfGroupKeys.values().map { it.toModel() }.toResponseEntity()

    override fun configurationGroupKeyGet(groupKey: String): ResponseEntity<List<ConfigurationEntrySchema>> =
        when(ConfGroupKeys.valueOfOrNull(groupKey)) {
            null -> mutableListOf<ConfigurationEntrySchema>().toResponseEntity()
            ConfGroupKeys.DEFAULT -> ConfKeys.values()
                .map { keySchema(it, configuration[it]) }
                .toResponseEntity()
        }

    override fun configurationGroupKeyKeyGet(groupKey: String, key: String): ResponseEntity<ConfigurationValue> {
        val groupKeyInstance = ConfGroupKeys.getGroupKey(groupKey)
        return ConfigurationValue(configuration[groupKeyInstance][groupKeyInstance.createKey(key)]).toResponseEntity()
    }

    override fun configurationGroupKeyKeyDelete(groupKey: String, key: String): ResponseEntity<Unit> =
        configuration.remove(ConfGroupKeys.getGroupKey(groupKey).createKey(key)).toResponseEntity()


    override fun configurationGroupKeyKeyPost(
        groupKey: String,
        key: String,
        configurationValue: ConfigurationValue
    ): ResponseEntity<Unit> {
        val groupKeyInstance = ConfGroupKeys.getGroupKey(groupKey)
        configuration[groupKeyInstance][groupKeyInstance.createKey(key)] = configurationValue.value
        return ResponseEntity.ok().build()
    }

}