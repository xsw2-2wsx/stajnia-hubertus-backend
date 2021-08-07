package net.jupw.hubertus.app.exceptions

class PasswordsDoNotMatchException(userId: Int)
    : ApplicationException("User with id $userId tried to change password, but provided wrong old password")