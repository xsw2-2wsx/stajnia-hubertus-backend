package net.jupw.hubertus.util.validation

import java.lang.Exception
import java.lang.IllegalStateException
import java.time.LocalTime
import java.util.*

class FailedRequirement(val value: String, val message: String)

class ValidationReport(val value: String, val failedRequirement: MutableList<FailedRequirement> = LinkedList())

typealias Requirement =  (String) -> List<FailedRequirement>

infix fun Requirement.and(other: Requirement): Requirement = { report ->
    LinkedList<FailedRequirement>().also {
        it.addAll(this(report))
        it.addAll(other(report))
    }
}

class RequirementBuilder {

    val notBlank: Requirement = {
        if (it.isBlank()) listOf(FailedRequirement(it, "wartość nie może być pusta"))

        listOf()
    }

    fun minLen(min: Int): Requirement = {
        if(it.length < min) listOf(FailedRequirement(it, "musi mieć długość conajmniej $min znaków"))
        emptyList()
    }

    fun maxLen(max: Int): Requirement = {
        if(it.length > max) listOf(FailedRequirement(it, "musi mieć długość co najwyżej $max znaków"))
        emptyList()
    }

    fun lenInRange(range: IntRange): Requirement = {
        if(it.length !in range) listOf(FailedRequirement(it, "musi mieć długość pomiędzy ${range.first} oraz ${range.last} (włącznie)"))
        emptyList()
    }

    val time: Requirement = {
        try {
            LocalTime.parse(it)
            emptyList()
        } catch (e: Exception) {
            listOf(FailedRequirement(it, "jest niepoprawnie sformatowana godziną"))
        }
    }

    val int: Requirement = {
        try {
            if(it.toDouble().compareTo(it.toInt()) != 0)
                throw IllegalStateException()
            emptyList()
        } catch (e: Exception) {
            listOf(FailedRequirement(it, "musi być liczbą całkowitą"))
        }
    }

    val number: Requirement = {
        if(it.toDoubleOrNull() == null)
            listOf(FailedRequirement(it, "musi być liczbą"))
        emptyList()
    }

    fun min(min: Double): Requirement = {
        try {
            if(it.toDouble() < min) throw IllegalStateException()
            emptyList()
        } catch (e: Exception) {
            listOf(FailedRequirement(it, "musi wynosić conajmniej $min"))
        }
    }

    fun max(max: Double): Requirement = {
        try {
            if(it.toDouble() > max) throw IllegalStateException()
            emptyList()
        } catch (e: Exception) {
            listOf(FailedRequirement(it, "musi wynosić co najwyżej $max"))
        }
    }

    fun inRange(range: ClosedRange<Double>): Requirement = {
        try {
            if(it.toDouble() !in range) throw IllegalStateException()
            emptyList()
        } catch (e: Exception) {
            listOf(FailedRequirement(it, "musi mieć wartość pomiędzy ${range.start} oraz ${range.endInclusive} (włącznie)"))
        }
    }
}

fun String.validate(conf: RequirementBuilder.() -> Requirement) {
    val builder = RequirementBuilder()
    val requirement = builder.conf()
    val report = ValidationReport(this)
    val failed = requirement(this)
    report.failedRequirement.addAll(failed)

    if(failed.isNotEmpty())
        throw ValidationException(report)
}