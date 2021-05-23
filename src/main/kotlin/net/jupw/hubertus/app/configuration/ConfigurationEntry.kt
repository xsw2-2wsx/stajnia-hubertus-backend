package net.jupw.hubertus.business.configuration

interface ConfigurationEntry {
    val key: ConfigurationKey

    val value: String
}