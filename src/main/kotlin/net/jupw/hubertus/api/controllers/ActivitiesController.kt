package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.Activity
import net.jupw.hubertus.api.models.ActivityConstraint
import net.jupw.hubertus.api.toResponseEntity
import net.jupw.hubertus.app.interactors.ActivityInteractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime

@RestController
class ActivitiesController : ActivitiesApi {

    @Autowired
    private lateinit var activityInteractor: ActivityInteractor

    override fun getActivityById(activityId: Int): ResponseEntity<Activity> =
        activityInteractor.findActivityById(activityId).toModel().toResponseEntity()

    override fun getActivities(): ResponseEntity<List<Activity>> =
        activityInteractor.findActivities().map { it.toModel() }.toMutableList().toResponseEntity()

    override fun deleteActivityById(activityId: Int): ResponseEntity<Unit> {
        activityInteractor.deleteActivity(activityId)
        return ResponseEntity.ok().build()
    }

    override fun createActivity(activity: Activity): ResponseEntity<Unit> {
        activityInteractor.saveActivity(0, activity.name, activity.description, activity.points.toDouble())
        return ResponseEntity.ok().build()
    }

    override fun modifyActivity(activityId: Int, activity: Activity): ResponseEntity<Unit> {
        activityInteractor.modifyActivity(activityId, activity.name, activity.description, activity.points.toDouble())
        return ResponseEntity.ok().build()
    }

    override fun getActivityConstraintsByActivityId(activityId: Int): ResponseEntity<List<ActivityConstraint>> =
        activityInteractor.findActivityConstraints(activityId).map { it.toModel() }.toResponseEntity()

    override fun setActivityConstraintsByActivityId(
        activityId: Int,
        activityConstraint: List<ActivityConstraint>
    ): ResponseEntity<Unit> {
        val constraints = activityConstraint.map { Pair(
          LocalTime.parse(it.startTime),
          LocalTime.parse(it.endTime),
        ) }.toSet()

        activityInteractor.setActivityConstraints(activityId, constraints)
        return ResponseEntity.ok().build()
    }

    override fun deleteActivityConstraintsByActivityId(activityId: Int): ResponseEntity<Unit> {
        activityInteractor.removeActivityConstraints(activityId)
        return ResponseEntity.ok().build()
    }
}