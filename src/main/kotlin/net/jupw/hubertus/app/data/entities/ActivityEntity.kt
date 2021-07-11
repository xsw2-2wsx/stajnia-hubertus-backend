package net.jupw.hubertus.app.data.entities

import javax.persistence.*

@Entity
@Table(name = "activities")
class ActivityEntity(

    @Id
    @GeneratedValue
    @Column(name = "activities_id")
    var id: Int,

    @Column(name = "activities_name")
    var name: String,

    @Column(name = "activities_description")
    var description: String,

    @Column(name = "activities_points")
    var points: Double,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "activity_constraints", joinColumns = [ JoinColumn(name = "activities_id" ) ])
    var constraints: Set<ActivityConstraintEmbeddable>,

)