package net.jupw.hubertus.app.configuration

import net.jupw.hubertus.util.Displayable

interface ConfigurationKey : Displayable {

    val key: String

    val defaultValue: String

    fun validate(value: String)

}