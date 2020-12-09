package top.ilum.amenity.data

data class Territory(
    val id: String,
    val name: String,
    val description: String,
    val user: User,
    val coordinates: Coordinates? = null,
    val longitude: List<Float>? = null,
    val latitude: List<Float>? = null

)

data class Coordinates(
    val longitude: Float,
    val latitude: Float
)