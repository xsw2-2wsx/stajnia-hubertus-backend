package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.configuration.ConfigurationEntry
import net.jupw.hubertus.app.configuration.ConfigurationGroupKey
import net.jupw.hubertus.app.configuration.ConfigurationKey

typealias ConfigurationGroupKeyModel = net.jupw.hubertus.api.models.ConfigurationGroupKey
typealias ConfigurationKeyModel = net.jupw.hubertus.api.models.ConfigurationKey
typealias ConfigurationEntryModel = net.jupw.hubertus.api.models.ConfigurationEntry
typealias ConfigurationEntrySchemaModel = net.jupw.hubertus.api.models.ConfigurationEntrySchema

fun ConfigurationGroupKey.toModel() = ConfigurationGroupKeyModel(
    key = key,
    name = displayName,
    description = description,
)


fun ConfigurationEntry.toModel() = ConfigurationEntryModel(
    key = key.key,
    value = value
)

fun ConfigurationKey.toModel() = ConfigurationKeyModel(
    key = key,
    name = displayName,
    description = description,
    defaultValue = defaultValue,

    )

fun keySchema(key: ConfigurationKey, value: String) = ConfigurationEntrySchemaModel(
    key = key.toModel(),
    value = value,
)