package net.jupw.hubertus.app.configuration

import net.jupw.hubertus.app.data.entities.ConfigurationEntryEntity
import net.jupw.hubertus.app.data.repositories.ConfigurationRepository

class ConfigurationGroupImpl(
    override val groupKey: ConfigurationGroupKey,
    private val confRepository: ConfigurationRepository,
    ) : ConfigurationGroup {

    override val entries: Collection<ConfigurationEntry>
        get() = confRepository.findByGroup(groupKey.key).map { it.toEntry() }

    override fun get(key: ConfigurationKey): String = confRepository
        .findByGroupAndKey(groupKey.key, key.key)
        ?.run { toEntry() }
        ?.value
        ?: key.defaultValue

    override fun getEntry(key: ConfigurationKey): ConfigurationEntry = confRepository
        .findByGroupAndKey(groupKey.key, key.key)
        ?.run { toEntry() }
        ?: ConfigurationEntryImpl(key, key.defaultValue)

    override fun set(key: ConfigurationKey, value: String) {
        key.validate(value)

        confRepository.deleteByKeyAndGroup(key.key, groupKey.key)
        confRepository.save(ConfigurationEntryEntity(0, key.key, value, groupKey.key))
    }

    override fun save(entry: ConfigurationEntry) {
        entry.key.validate(entry.value)
        confRepository.deleteByKeyAndGroup(entry.key.key, groupKey.key)
        confRepository.save(entry.toEntity())
    }

    override fun remove(key: ConfigurationKey) = confRepository.deleteByKeyAndGroup(key.key, groupKey.key)

    private fun ConfigurationEntryEntity.toEntry() = ConfigurationEntryImpl(
        groupKey.createKey(key),
        value,
    )

    private fun ConfigurationEntry.toEntity() = ConfigurationEntryEntity(0, key.key, value, groupKey.key)
}