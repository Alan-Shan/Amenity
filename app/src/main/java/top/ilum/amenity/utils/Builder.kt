package top.ilum.amenity.utils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Builder {
    private val client = OkHttpClient.Builder().addInterceptor(TokenInterceptor())
        .authenticator(AccessTokenAuthenticator()).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://95.216.218.198/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}