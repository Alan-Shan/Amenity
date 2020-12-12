package top.ilum.amenity.data

data class Marker(
    val id: String? = null,
    val name: String,
    val description: String,
    val user: String,
    val longitude: Double,
    val latitude: Double,
    val territory: String

)
