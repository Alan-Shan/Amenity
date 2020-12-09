package top.ilum.amenity.data

data class Register(
    val username: String,
    val password: String,
    val name: String,
    val email: String
)

data class APIResult(
    val status: String,
    val optionalID: String? = null
)