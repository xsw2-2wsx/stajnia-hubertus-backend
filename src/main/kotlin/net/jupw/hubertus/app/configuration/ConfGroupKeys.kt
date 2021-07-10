package net.jupw.hubertus.app.configuration

import net.jupw.hubertus.app.configuration.exceptions.ConfigurationGroupDoesNotExistException
import net.jupw.hubertus.app.configuration.exceptions.InvalidConfigurationKeyException

enum class ConfGroupKeys : ConfigurationGroupKey {

    DEFAULT {
        override fun createKey(key: String): ConfigurationKey =
            ConfKeys.valueOfOrNull(key)?: throw InvalidConfigurationKeyException(this, key)

        override val displayName = "Konfiguracja aplikacji"

        override val description: String
            get() = super.description
    }

    ;

    companion object {
        fun valueOfOrNull(value: String): ConfGroupKeys? = try {
            valueOf(value)
        } catch(e: Exception) {
            null
        }

        fun getGroupKey(value: String): ConfigurationGroupKey =
            valueOfOrNull(value)?: throw ConfigurationGroupDoesNotExistException(value)

    }


    override val key: String
        get() = name
}