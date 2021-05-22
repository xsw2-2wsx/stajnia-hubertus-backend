package net.jupw.hubertus.configuration

interface Configuration : ConfigurationGroup {

    operator fun get(key: ConfigurationGroupKey): ConfigurationGroup

    val group: Collection<ConfigurationGroup>

}