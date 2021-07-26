package net.jupw.hubertus.app.entities

class RoleImpl(
    override var id: Int,
    override val displayName: String,
    override val description: String,
    override var authorities: Set<Authority>,
) : Role