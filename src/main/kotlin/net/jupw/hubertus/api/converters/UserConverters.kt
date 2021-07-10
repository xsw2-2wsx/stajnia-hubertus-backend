package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.entities.User

typealias UserModel = net.jupw.hubertus.api.models.User

fun User.convertToModel(): UserModel = UserModel(
    id = id,
    name = name,
    email = email,
    phone = phone,
    locked = isLocked,
)