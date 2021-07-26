package net.jupw.hubertus.api.converters

import net.jupw.hubertus.app.entities.Authority
import net.jupw.hubertus.app.entities.Role

typealias RoleModel = net.jupw.hubertus.api.models.Role
typealias AuthorityModel = net.jupw.hubertus.api.models.Authority

fun Role.toModel() = RoleModel(displayName, description, id)

fun Authority.toModel() = AuthorityModel(key, displayName, description)