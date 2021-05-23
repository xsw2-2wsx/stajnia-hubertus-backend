package net.jupw.hubertus.business.configuration

import net.jupw.hubertus.util.Displayable

sealed interface ConfigurationKey : Displayable {
    val key: String

    fun validate(value: String)

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    enum class Keys : ConfigurationKey {
        MAX_POINT_AMOUNT {

        }


    }

}