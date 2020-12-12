package top.ilum.amenity.utils

import retrofit2.Call
import retrofit2.http.*
import top.ilum.amenity.data.*

interface Endpoints {
    companion object {
        private const val USER = "/api/users/{id}"
        private const val USERS = "/api/users"
        private const val REGISTER = "/register"
        private const val TERRITORIES = "/api/territories"
        private const val TERRITORY = "/api/territories/{id}"
        private const val MARKERS = "/api/markers"
        private const val MARKER = "/api/markers/{id}"
        private const val COMMUNITIES = "/api/communities"


    }

    @GET(USERS)
    fun getUsers(): Call<List<User>>

    @GET(USER)
    fun getUser(@Path("id") id: String): Call<User>

    @Headers("Content-Type: application/json")
    @POST(REGISTER)
    fun register(@Body() data: Register): Call<APIResult>

    @GET(TERRITORIES)
    fun getTerritories(): Call<List<Territory>>

    @GET(TERRITORY)
    fun getTerritory(@Path("id") id: String): Call<Territory>

    @Headers("Content-Type: application/json")
    @POST(TERRITORIES)
    fun postTerritory(@Body() data: Territory): Call<APIResult>

    @GET(MARKERS)
    fun getMarkers(): Call<List<Marker>>

    @GET(MARKER)
    fun getMarker(@Path("id") id: String): Call<Marker>

    @Headers("Content-Type: application/json")
    @POST(MARKERS)
    fun postMarker(@Body data: Marker): Call<APIResult>

    @GET(COMMUNITIES)
    fun getCommunities(): Call<List<Communities>>

    @Headers("Content-Type: application/json")
    @POST(COMMUNITIES)
    fun postCommunities(@Body data: Communities): Call<APIResult>
}