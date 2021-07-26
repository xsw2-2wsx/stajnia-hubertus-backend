package net.jupw.hubertus.app.exceptions

class UserDoesNotHaveSpecifiedRoleException(userId: Int, roleId: Int)
    : Exception("User with id $userId does not have role with id $roleId")