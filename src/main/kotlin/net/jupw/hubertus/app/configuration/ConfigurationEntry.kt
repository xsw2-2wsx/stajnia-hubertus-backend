package net.jupw.hubertus.app.configuration

interface ConfigurationEntry {
    val key: ConfigurationKey

    val value: String
}