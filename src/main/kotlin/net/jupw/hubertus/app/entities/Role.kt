package net.jupw.hubertus.app.entities

import net.jupw.hubertus.util.Displayable

interface Role : Displayable {

    var id: Int

    var authorities: Set<Authority>

}