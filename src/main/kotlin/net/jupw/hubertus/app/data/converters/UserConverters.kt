package net.jupw.hubertus.app.data.converters

import net.jupw.hubertus.app.data.entities.UserEntity
import net.jupw.hubertus.app.entities.User
import net.jupw.hubertus.app.entities.UserImpl

fun UserEntity.toUser() = UserImpl(
    id,
    name,
    password,
    phone,
    email,
    isLocked,
    roles.flatMap { it.authorities }
)