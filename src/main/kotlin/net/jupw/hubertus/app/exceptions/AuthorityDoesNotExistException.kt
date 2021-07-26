package net.jupw.hubertus.app.exceptions

class AuthorityDoesNotExistException(key: String) : Exception("Authority with id $key does not exist")