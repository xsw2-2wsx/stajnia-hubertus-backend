package net.jupw.hubertus.app.exceptions

class UserNotFoundException : ApplicationException {

    constructor(username: String) : super("User with username $username does not exist.")

    constructor(id: Int) : super("User does not exist")

}