package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.entities.User

typealias UserModel = net.jupw.hubertus.api.models.User

fun User.convertToModel(): UserModel = UserModel().also {
    it.id = id
    it.name = name
    it.email = email
    it.phone = phone
    it.isLocked = isLocked
}