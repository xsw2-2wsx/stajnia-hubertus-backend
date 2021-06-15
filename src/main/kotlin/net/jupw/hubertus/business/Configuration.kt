package net.jupw.hubertus.business

interface Configuration {

    operator fun get(key: Key): String

    companion object {
        enum class Key {
            MAX_POINTS,
            MIN_BOOKING_PRECEDENCE_MS
        }
    }
}

typealias ConfKeys = Configuration.Companion.Key