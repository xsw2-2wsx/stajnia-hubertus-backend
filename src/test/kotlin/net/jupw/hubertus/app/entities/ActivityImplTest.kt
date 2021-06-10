package net.jupw.hubertus.app.entities

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

internal class ActivityImplTest {

    private fun testActivity(constraints: List<ActivityConstraint>) = ActivityImpl(
        -1,
        "",
        "",
        -1.0,
        constraints
    )

    @Test
    fun `Test is allowed for fully include constraint`() {
        val activity = testActivity(listOf(
            ActivityConstraintImpl(0, LocalTime.of(9, 0), LocalTime.of(15, 0))
        ))

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)),
            )
        }

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)),
            )
        }

        assertFalse {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 1)),
            )
        }

        assertFalse {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 59)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)),
            )
        }

    }

    @Test
    fun `Test is allowed with constraint changes`() {

        val constraints = LinkedList<ActivityConstraintImpl>().apply {
            add(ActivityConstraintImpl(0, LocalTime.of(9, 0), LocalTime.of(10, 0)))
            add(ActivityConstraintImpl(0, LocalTime.of(11, 0), LocalTime.of(12, 0)))
        }

        val activity = testActivity(constraints)

        assertFalse {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 5)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 55)),
            )
        }

        assertFalse {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 5)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)),
            )
        }

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 5)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
            )
        }

        constraints.add(
            ActivityConstraintImpl(0, LocalTime.of(10, 0), LocalTime.of(11, 0))
        )

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 5)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 55)),
            )
        }

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 5)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)),
            )
        }
    }

    @Test
    fun `Test date change`() {
        val constraints = LinkedList<ActivityConstraintImpl>().apply {
            add(ActivityConstraintImpl(0, LocalTime.of(23, 0), LocalTime.of(1, 0)))
            add(ActivityConstraintImpl(0, LocalTime.of(1, 0), LocalTime.of(2, 0)))
        }

        val activity = testActivity(constraints)

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 30)),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(2, 0)),
            )
        }

        assertFalse {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 30)),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(2, 30)),
            )
        }

        assertFalse {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 30)),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(2, 0)),
            )
        }

    }

    @Test
    fun `test date change on 0h0m`() {
        val constraints = LinkedList<ActivityConstraintImpl>().apply {
            add(ActivityConstraintImpl(0, LocalTime.of(22, 0), LocalTime.of(0, 0)))
        }

        val activity = testActivity(constraints)

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0)),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)),
            )
        }

        assertFalse {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0)),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 1)),
            )
        }
    }

    @Test
    fun `test ranges when start before end`() {
        val constraints = LinkedList<ActivityConstraintImpl>().apply {
            add(ActivityConstraintImpl(0, LocalTime.of(10, 0), LocalTime.of(15, 0)))
        }

        val activity = testActivity(constraints)

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)),
            )
        }
    }

    @Test
    fun `test ranges when end before start`() {
        val constraints = LinkedList<ActivityConstraintImpl>().apply {
            add(ActivityConstraintImpl(0, LocalTime.of(22, 0), LocalTime.of(1, 0)))
        }

        val activity = testActivity(constraints)

        assertTrue {
            activity.isAllowed(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0)),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(1, 0)),
            )
        }
    }
}