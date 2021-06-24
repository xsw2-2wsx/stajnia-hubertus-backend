package net.jupw.hubertus.app.configuration

enum class ConfGroupKeys : ConfigurationGroupKey {

    DEFAULT {
        override fun createKey(key: String): ConfigurationKey =
            ConfKeys.valueOf(key)

        override val displayName = "Konfiguracja aplikacji"

        override val description: String
            get() = super.description
    }

    ;

    companion object {
        fun valueOfOrNull(value: String): ConfGroupKeys? = try {
            valueOf(value)
        } catch(e: Exception) {
            null
        }

    }


    override val key: String
        get() = name
}