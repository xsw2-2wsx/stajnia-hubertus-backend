package net.jupw.hubertus.app.configuration

interface Configuration : ConfigurationGroup, net.jupw.hubertus.business.Configuration {

    operator fun get(key: ConfigurationGroupKey): ConfigurationGroup

    val group: Collection<ConfigurationGroup>



}