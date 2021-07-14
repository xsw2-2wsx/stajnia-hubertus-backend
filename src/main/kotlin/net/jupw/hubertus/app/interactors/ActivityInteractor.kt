package net.jupw.hubertus.app.interactors

import net.jupw.hubertus.app.data.converters.toActivity
import net.jupw.hubertus.app.data.converters.toConstraint
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
        constraints: Set<Pair<LocalTime, LocalTime>>
    ) =
        activityRepository.save(ActivityEntity(
            id, name, description, points,
            constraints.map { ActivityConstraintEmbeddable(it.first, it.second) }.toSet()
        ))

    @Transactional
    fun setConstraints(id: Int, constraint: Set<Pair<LocalTime, LocalTime>>) {
        val activity = activityRepository.findByIdOrNull(1)?:
            throw ActivityDoesNotExistException(id)

        activity.constraints = constraint.map { it.toConstraint() }.toSet()
    }

    @Transactional
    fun modifyActivity(id: Int, name: String, description: String, points: Double, constraints: Set<Pair<LocalTime, LocalTime>>) {
        val activityToEdit = activityRepository.findByIdOrNull(id)?: throw ActivityDoesNotExistException(id)
        activityToEdit.let {
            it.id = id
            it.name = name
            it.description = description
            it.points = points
            it.constraints = constraints.map { p ->  p.toConstraint() }.toSet()
        }
    }

    fun deleteActivity(id: Int) =
        if(activityRepository.existsById(id)) activityRepository.deleteById(id)
        else throw ActivityDoesNotExistException(id)

}