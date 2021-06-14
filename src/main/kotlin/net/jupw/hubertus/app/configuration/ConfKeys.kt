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
            to konieczne, system odmówi użytkowniką składania nowych rezerwacji.
        """.trimIndent()

        override fun validate(value: String) = value.validate {
            notBlank and number and min(0.0)
        }
    },
    MIN_BOOKING_PRECEDENCE_HOURS {
        override val displayName: String = "Minimalne wyprzedzenie rezerwacji"

        override val defaultValue: String = "3600000"

        override val description: String = ". Wartość 0 wyłącza tą opcję"

        override fun validate(value: String) = value.validate {
            notBlank and number and min(0.0)
        }

    },
    ;



    override val key: String
        get() = name
}