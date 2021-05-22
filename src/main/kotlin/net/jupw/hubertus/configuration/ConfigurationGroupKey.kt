package net.jupw.hubertus.configuration

import net.jupw.hubertus.util.Displayable

interface ConfigurationGroupKey : Displayable {

    val key: String

    fun createKey(key: String): ConfigurationKey

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}