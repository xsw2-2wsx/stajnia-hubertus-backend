package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.configuration.ConfigurationEntry
import net.jupw.hubertus.app.configuration.ConfigurationGroupKey
import net.jupw.hubertus.app.configuration.ConfigurationKey

typealias ConfigurationGroupKeyModel = net.jupw.hubertus.api.models.ConfigurationGroupKey
typealias ConfigurationKeyModel = net.jupw.hubertus.api.models.ConfigurationKey
typealias ConfigurationValueModel = net.jupw.hubertus.api.models.ConfigurationValue
typealias ConfigurationEntrySchemaModel = net.jupw.hubertus.api.models.ConfigurationEntrySchema

fun ConfigurationGroupKey.toModel() = ConfigurationGroupKeyModel(
    key = key,
    name = displayName,
    description = description,
)


fun ConfigurationEntry.toModel() = ConfigurationValueModel(
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