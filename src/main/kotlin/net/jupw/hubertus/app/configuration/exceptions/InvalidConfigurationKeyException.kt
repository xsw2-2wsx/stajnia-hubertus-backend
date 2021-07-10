package net.jupw.hubertus.app.configuration.exceptions

import net.jupw.hubertus.app.configuration.ConfigurationGroupKey

class InvalidConfigurationKeyException(val groupKey: ConfigurationGroupKey, val key: String)
    : ConfigurationException("Invalid configuration key '$key' in group '${groupKey.key}'")