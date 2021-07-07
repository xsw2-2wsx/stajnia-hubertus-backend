package net.jupw.hubertus.app.configuration

import net.jupw.hubertus.app.data.repositories.ConfigurationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class ConfigurationImpl : Configuration {

    @Autowired
    private lateinit var confRepository: ConfigurationRepository

    private val groups: MutableMap<ConfigurationGroupKey, ConfigurationGroup> = HashMap()

    @PostConstruct
    fun init() = ConfGroupKeys.values().forEach {
        groups[it] = ConfigurationGroupImpl(it, confRepository)
    }

    override fun get(key: ConfigurationGroupKey): ConfigurationGroup = groups[key]!!

    override val group: Collection<ConfigurationGroup> = groups.values

    override val groupKey: ConfigurationGroupKey = ConfGroupKeys.DEFAULT

    override val entries: Collection<ConfigurationEntry>
        get() = groups[ConfGroupKeys.DEFAULT]!!.entries

    override fun get(key: ConfigurationKey): String = groups[ConfGroupKeys.DEFAULT]!![key]

    override fun getEntry(key: ConfigurationKey): ConfigurationEntry = groups[ConfGroupKeys.DEFAULT]!!.getEntry(key)

    override fun set(key: ConfigurationKey, value: String) = groups[ConfGroupKeys.DEFAULT]!!.set(key, value)

    override fun save(entry: ConfigurationEntry) = groups[ConfGroupKeys.DEFAULT]!!.save(entry)

    override fun remove(key: ConfigurationKey) = groups[ConfGroupKeys.DEFAULT]!!.remove(key)

    override fun get(key: net.jupw.hubertus.business.Configuration.Companion.Key): String =
        groups[ConfGroupKeys.DEFAULT]!![ConfGroupKeys.DEFAULT.createKey(key.name)]
}