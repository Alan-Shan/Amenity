package top.ilum.amenity.data

data class Marker(
    val id: String? = null,
    val name: String,
    val description: String,
    val user: User,
    val longitude: Float,
    val latitude: Float,
    val territory: Territory

)
