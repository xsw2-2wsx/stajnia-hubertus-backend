package net.jupw.hubertus.app.data.entities

import javax.persistence.*

@Entity
@Table(name = "configuration")
class ConfigurationEntryEntity(
    @Id
    @GeneratedValue
    @Column(name = "configuration_id")
    var id: Int,

    @Column(name = "configuration_key")
    var key: String,

    @Column(name = "configuration_value")
    var value: String,

    @Column(name = "configuration_group")
    var group: String,

)