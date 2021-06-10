package net.jupw.hubertus.app.data.entities

import javax.persistence.*

@Entity
@Table(name = "activity_constraints")
class ActivityEntity(

    @Id
    @GeneratedValue
    @Column(name = "activities_id")
    var id: Int,

    @Column(name = "name")
    var name: String,

    @Column(name = "description")
    var description: String,

    @Column(name = "points")
    var points: Double,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "activity_constraints", joinColumns = [ JoinColumn(name = "activities_id" ) ])
    var constraints: List<ActivityConstraintEmbeddable>,

)