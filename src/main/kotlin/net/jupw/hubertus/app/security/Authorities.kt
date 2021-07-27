package net.jupw.hubertus.app.security

import net.jupw.hubertus.app.entities.Authority
import kotlin.Exception

enum class Authorities : Authority {
    MANAGE_ACTIVITIES {
        override val displayName: String = "Zarządzanie aktywnościami"

        override val description: String = """
            Dodawanie nowych aktywności, uswanie i modyfikacja instniejących, ustawianie ograniczeń czasowych.
        """.trimIndent()
    },
    USE_BOOKING {
        override val displayName: String = "Składanie rezerwacji"

        override val description: String = """
            Tworzenie nowych rezerwacji oraz odwoływanie swoich
        """.trimIndent()
    },
    MANAGE_BOOKINGS {
        override val displayName: String = "Zarządzanie rezerwacjami"

        override val description: String = """
            Możliwość odwołania każdej rezerwacji
        """.trimIndent()
    },
    MANAGE_ROLES {
        override val displayName: String = "Zarządzanie rolami"

        override val description: String = """
            Dodawanie nowych ról, uswanie i modyfikacja istniejących, dodawanie uprawnień do ról.
        """.trimIndent()
    },
    MANAGE_USERS {
        override val displayName: String = "Zarządzanie użytkownikami"

        override val description: String = """
            Dodawanie, usuwanie, blokowanie i modyfikacja użytkowników, dodawanie im ról
        """.trimIndent()
    },
    RESET_ANY_PASSWORD {
        override val displayName: String = "Resetowanie hasła"

        override val description: String = """
            Resetowanie hasła każdemu użytkownikowi
        """.trimIndent()
    },


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