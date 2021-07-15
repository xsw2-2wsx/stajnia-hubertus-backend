package net.jupw.hubertus.app.exceptions

class BookingDoesNotExistException(id: Int) : ApplicationException("Booking with id $id does not exist")