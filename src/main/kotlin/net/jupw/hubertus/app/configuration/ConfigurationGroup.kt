package net.jupw.hubertus.app.configuration

interface ConfigurationGroup {
    val key: ConfigurationGroupKey

    val entries: Collection<ConfigurationEntry>

    operator fun get(key: ConfigurationKey): String

    fun getEntry(key: ConfigurationKey): ConfigurationEntry

    operator fun set(key: ConfigurationKey, value: String)

    fun save(entry: ConfigurationEntry)

    fun remove(key: ConfigurationKey)
}