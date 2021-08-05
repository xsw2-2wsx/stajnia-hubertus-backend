package net.jupw.hubertus.app.exceptions

class NoProfilePictureException(userId: Int) : ApplicationException("User with id $userId does not have a profile picture")