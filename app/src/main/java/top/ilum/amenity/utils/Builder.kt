package top.ilum.amenity.utils

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Builder {
    fun <T> buildService(service: Class<T>, context: Context): T {
        val client = OkHttpClient.Builder().addInterceptor(TokenInterceptor())
            .authenticator(AccessTokenAuthenticator(context)).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(service)
    }
}