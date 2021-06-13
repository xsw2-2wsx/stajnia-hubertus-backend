package net.jupw.hubertus.app.interactors

import net.jupw.hubertus.app.data.entities.ActivityConstraintEmbeddable
import net.jupw.hubertus.app.data.entities.ActivityEntity
import net.jupw.hubertus.app.data.repositories.ActivityRepository
import net.jupw.hubertus.app.entities.Activity
import net.jupw.hubertus.app.entities.ActivityConstraint
import net.jupw.hubertus.app.entities.ActivityConstraintImpl
import net.jupw.hubertus.app.entities.ActivityImpl
import net.jupw.hubertus.app.exceptions.ActivityDoesNotExistException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalTime
import javax.transaction.Transactional

@Service
class ActivityInteractor {

    @Autowired
    private lateinit var activityRepository: ActivityRepository

    fun findActivities(): List<Activity> = activityRepository.findAll().map { it.toActivity() }

    fun findActivityById(id: Int): Activity =
        activityRepository
            .findByIdOrNull(id)
            ?.toActivity()
            ?: throw ActivityDoesNotExistException(id)

    fun saveActivity(
        id: Int,
        name: String,
        description: String,
        points: Double,
        constraints: List<Pair<LocalTime, LocalTime>>
    ) =
        activityRepository.save(ActivityEntity(
            id, name, description, points,
            constraints.map { ActivityConstraintEmbeddable(it.first, it.second) }
        ))

    @Transactional
    fun setConstraints(id: Int, constraint: List<Pair<LocalTime, LocalTime>>) {
        val activity = activityRepository.findByIdOrNull(1)?:
            throw ActivityDoesNotExistException(id)

        activity.constraints = constraint.map { it.toConstraint() }
    }

    @Transactional
    fun modifyActivity(id: Int, name: String, description: String, points: Double, constraints: List<Pair<LocalTime, LocalTime>>) {
        val activityToEdit = activityRepository.findByIdOrNull(id)?: throw ActivityDoesNotExistException(id)
        activityToEdit.let {
            it.id = id
            it.name = name
            it.description = description
            it.points = points
            it.constraints = constraints.map { p ->  p.toConstraint() }
        }
    }

    fun deleteActivity(id: Int) =
        if(activityRepository.existsById(id)) activityRepository.deleteById(id)
        else throw ActivityDoesNotExistException(id)

    fun ActivityEntity.toActivity() = ActivityImpl(
        id,
        name,
        description,
        points,
        constraints.map { it.toActivityConstraint() }
    )

    fun Activity.toEntity() = ActivityEntity(
        0,
        name,
        description,
        points,
        constraints.map { it.toEntity() }
    )

    fun ActivityConstraintEmbeddable.toActivityConstraint() = ActivityConstraintImpl(
        startTime, endTime
    )

    fun ActivityConstraint.toEntity() = ActivityConstraintEmbeddable(
        startTime, endTime
    )

    fun Pair<LocalTime, LocalTime>.toConstraint() = ActivityConstraintEmbeddable(first, second)

}