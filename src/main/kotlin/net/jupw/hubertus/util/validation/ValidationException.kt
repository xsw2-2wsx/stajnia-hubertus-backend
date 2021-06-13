package net.jupw.hubertus.util.validation

class ValidationException(val report: ValidationReport) : Exception("Nieprawidłowa wartość")