package net.jupw.hubertus.business

interface Configuration {

    operator fun get(key: Key): String

    companion object {
        enum class Key {
            MAX_POINTS,
        }
    }
}

typealias ConfKeys = Configuration.Companion.Key