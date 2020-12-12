package top.ilum.amenity.data

data class SocketData(
    val room: String,
    val token: String,
    val msg: String? = null
)

data class Message(
    val msg: String,
    val name: String? = null
)

data class Event(
    val type: Int,
    val msg: String,
    val name: String? = null
)