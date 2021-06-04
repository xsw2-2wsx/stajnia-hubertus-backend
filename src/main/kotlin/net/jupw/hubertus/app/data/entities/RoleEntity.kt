package net.jupw.hubertus.app.data.entities

import javax.persistence.*

@Entity
@Table(name = "roles")
class RoleEntity(

    @Id
    @GeneratedValue
    @Column(name = "roles_id")
    var id: Int,

    @Column(name = "roles_name")
    var name: String,

    @ElementCollection
    @CollectionTable(name = "authorities", joinColumns = [ JoinColumn(name = "roles_id") ])
    var authorities: List<String>
)
