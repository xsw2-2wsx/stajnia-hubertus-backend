package net.jupw.hubertus.app.configuration

import net.jupw.hubertus.util.validation.and
import net.jupw.hubertus.util.validation.validate

enum class ConfKeys : ConfigurationKey {

    MAX_POINTS {
        override val displayName: String = "Maksymalna ilość punktów zajętości"

        override val defaultValue: String = "6"

        override val description: String = """
            Zajętość obliczana jest poprzez zsumowanie punktów aktywności wszystkich rezerwacji, które są w danym
            momencie aktywne. $displayName oznacza liczbę punktów która nie może zostać przekroczona. Jeśli będzie 
            to konieczne, system odmówi użytkownikom składania nowych rezerwacji.
        """.trimIndent()

        override fun validate(value: String) = value.validate {
            notBlank and number and min(0.0)
        }
    },
    MIN_BOOKING_PRECEDENCE_MS {
        override val displayName: String = "Minimalne wyprzedzenie rezerwacji"

        override val defaultValue: String = "3600000"

        override val description: String = "$displayName. Wartość 0 wyłącza tą opcję"

        override fun validate(value: String) = value.validate {
            notBlank and number and min(0.0)
        }

    },

    BOOKING_HOURS_START {
        override val displayName: String = "Początkowa godzina dozwolonych czasów rezerwacji"

        override val defaultValue: String = "06:00:00"

        override val description: String = """Pierwsza dozwolona godzina rezerwacji, dalsze są obliczane dodając do niej interwał"""

        override fun validate(value: String) = value.validate { notBlank and time }

    },

    BOOKING_INTERVAL_MS {
        override val displayName: String = "Interwał dozwoloncyh godzin"

        override val defaultValue: String = "1800000"

        override fun validate(value: String) = value.validate { int and min(0.0) }

        override val description: String = """
            Interwał dodawany do godziny początkowej w celu uzyskania kolejnych dozwolonych godzin. Przykładowo, przy 
            godzinie początkowej 6:00 i interwale 30 min dozwolone godziny wynosić będą: 6:00, 6:30, 7:00, 7:30 ...
        """.trimIndent()
    },

    BOOKING_HORUS_END {
        override val displayName: String = "Ostatnia dozwolona godzina rezerwacji"

        override val defaultValue: String = "23:00:00"

        override val description: String = """
            Ostatnia dozwolona godzina rezerwacji, nie może zostać przekroczona
        """.trimIndent()

        override fun validate(value: String) = value.validate { notBlank and time }

    }
    ;

    companion object {
        fun valueOfOrNull(value: String): ConfigurationKey? = try {
            valueOf(value)
        } catch(e: Exception) {
            null
        }
    }



    override val key: String
        get() = name
}