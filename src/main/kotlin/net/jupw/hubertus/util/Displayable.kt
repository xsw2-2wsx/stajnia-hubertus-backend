package net.jupw.hubertus.util

interface Displayable {
    val displayName: String

    val defaultValue: String

    val description: String
        get() = displayName
}