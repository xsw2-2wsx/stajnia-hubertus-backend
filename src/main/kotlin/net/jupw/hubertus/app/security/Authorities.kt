package net.jupw.hubertus.app.security

import net.jupw.hubertus.app.entities.Authority
import kotlin.Exception

enum class Authorities : Authority {
    ;

    override val key: String
        get() = name

    companion object {
        fun readAuthority(key: String) = try {
            valueOf(key)
        } catch (e: Exception) {
            throw IllegalStateException("No such authority: $key", e)
        }

        fun valueOfOrNull(key: String): Authority? = try {
            valueOf(key)
        } catch (e: Exception) {
            null
        }
    }
}