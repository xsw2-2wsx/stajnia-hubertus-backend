package net.jupw.hubertus.app.data.entities

import javax.persistence.*

@Entity
@Table(name = "users")
class UserEntity (

    @Id
    @GeneratedValue
    @Column(name = "users_id")
    var id: Int,

    @Column(name = "users_name")
    var name: String,

    @Column(name = "users_phone")
    var phone: String,

    @Column(name = "users_email")
    var email: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(
            name = "users_id",
            referencedColumnName = "users_id"
        )],
    )
    var roles: List<RoleEntity>
)