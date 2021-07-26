package net.jupw.hubertus.app.exceptions

class RoleDoesNotExistException(id: Int) : Exception("Role with id $id not found")