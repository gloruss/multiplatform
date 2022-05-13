package co.touchlab.kampkit.response

import kotlinx.serialization.Serializable

@Serializable
data class Worker(
    val id : Long,
    val name : String,
    val uuid : String
)



fun Worker.toDB() : co.touchlab.kampkit.db.Worker{
    return co.touchlab.kampkit.db.Worker(id ,name, uuid)
}


fun co.touchlab.kampkit.db.Worker.toObject() : Worker{
    return Worker(id, name, uuid)
}

