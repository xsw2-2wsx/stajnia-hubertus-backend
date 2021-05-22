package net.jupw.hubertus.configuration

interface ConfigurationEntry {
    val key: ConfigurationKey

    val value: String
}