package net.jupw.hubertus.app.exceptions

class ActivityDoesNotExistException(val id: Int) :
    ApplicationException("Activity with id $id does not exist")