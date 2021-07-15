package net.jupw.hubertus.app.exceptions

class NotBookingOwnerException(bookingId: Int, userId: Int) :
    ApplicationException("An user with id $userId tried to modify the booking with id  $bookingId")