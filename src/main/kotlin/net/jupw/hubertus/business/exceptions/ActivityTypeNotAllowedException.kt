package net.jupw.hubertus.business.exceptions

import net.jupw.hubertus.business.entities.ActivityType

class ActivityTypeNotAllowedException(type: ActivityType)
    : BookingException("Activity ${type.displayName} is not allowed at this time.")