package net.jupw.hubertus.configuration

import net.jupw.hubertus.util.Displayable

interface ConfigurationKey : Displayable {
    val key: String

    fun validate(value: String)

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

}