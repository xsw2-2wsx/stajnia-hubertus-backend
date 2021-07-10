package net.jupw.hubertus.app.configuration.exceptions

class ConfigurationGroupDoesNotExistException(val groupKey: String)
    : ConfigurationException("Configuration group with key '$groupKey' does not exist")