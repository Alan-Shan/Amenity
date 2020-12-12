package top.ilum.amenity.data

data class Territory(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val user: String,
    val coordinates: Coordinates? = null,
    val longitude: List<Double>? = null,
    val latitude: List<Double>? = null

)

data class Coordinates(
    val longitude: Double,
    val latitude: Double
)