package net.jupw.hubertus.api.controllers

import net.jupw.hubertus.api.converters.toModel
import net.jupw.hubertus.api.models.Activity
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

    override fun activitiesActivityIdGet(activityId: Int): ResponseEntity<Activity> =
        activityInteractor.findActivityById(activityId).toModel().toResponseEntity()

    override fun activitiesGet(): ResponseEntity<List<Activity>> =
        activityInteractor.findActivities().map { it.toModel() }.toMutableList().toResponseEntity()

    override fun activitiesDelete(activityId: Int): ResponseEntity<Unit> {
        activityInteractor.deleteActivity(activityId)
        return ResponseEntity.ok().build()
    }

    override fun activitiesPost(activity: Activity): ResponseEntity<Unit> {
        activityInteractor.saveActivity(activity.id, activity.name, activity.description, activity.points.toDouble(), activity.constraints.map {
            Pair(LocalTime.parse(it.startTime), LocalTime.parse(it.endTime))
        }.toSet())
        return ResponseEntity.ok().build()
    }

    override fun activitiesPut(activity: Activity): ResponseEntity<Unit> {
        activityInteractor.modifyActivity(activity.id, activity.name, activity.description, activity.points.toDouble(), activity.constraints.map {
            Pair(LocalTime.parse(it.startTime), LocalTime.parse(it.endTime))
        }.toSet())
        return ResponseEntity.ok().build()
    }
}