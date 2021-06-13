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

    override fun activitiesGet(): ResponseEntity<MutableList<Activity>> =
        activityInteractor.findActivities().map { it.toModel() }.toMutableList().toResponseEntity()

    override fun activitiesDelete(activityId: Int): ResponseEntity<Void> {
        activityInteractor.deleteActivity(activityId)
        return ResponseEntity.ok().build()
    }

    override fun activitiesPost(a: Activity): ResponseEntity<Void> {
        activityInteractor.saveActivity(a.id, a.name, a.description, a.points.toDouble(), a.constraints.map {
            Pair(LocalTime.parse(it.startTime), LocalTime.parse(it.endTime))
        })
        return ResponseEntity.ok().build()
    }

    override fun activitiesPut(a: Activity): ResponseEntity<Void> {
        activityInteractor.modifyActivity(a.id, a.name, a.description, a.points.toDouble(), a.constraints.map {
            Pair(LocalTime.parse(it.startTime), LocalTime.parse(it.endTime))
        })
        return ResponseEntity.ok().build()
    }
}