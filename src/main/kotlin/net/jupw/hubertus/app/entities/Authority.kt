package net.jupw.hubertus.app.entities

import net.jupw.hubertus.util.Displayable

interface Authority : Displayable {
    val key: String
}