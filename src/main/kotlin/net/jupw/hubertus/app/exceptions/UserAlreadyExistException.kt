package net.jupw.hubertus.app.exceptions

class UserAlreadyExistException(val username: String)
    : ApplicationException("User with username $username already exists")