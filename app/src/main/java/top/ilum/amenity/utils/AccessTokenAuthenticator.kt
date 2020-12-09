package top.ilum.amenity.utils

import android.util.Log
import okhttp3.*
import org.json.JSONObject

/**
Token refresher on 401
 */
class AccessTokenAuthenticator() : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val token = refreshToken()
            if (token != null) {
                return token.let {
                    response.request
                        .newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", it)
                        .build()
                }
            }
            return null
        }
    }

    private fun refreshToken(): String? {
        var rt = SharedPrefs.refreshToken
        if (rt != null) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://95.216.218.198/refresh_token").addHeader("Authorization", rt)
                .build()
            client.newCall(request).execute().use { response ->
                if (response.code == 200) {
                    val tokenBody = response.body?.string().toString()
                    val tokenJson = JSONObject(tokenBody)
                    val token = tokenJson.getString("token")
                    val refreshBody = response.body?.string().toString()
                    val refreshJson = JSONObject(refreshBody)
                    rt = refreshJson.getString("refresh_token")
                    // Cascade used on a reason, don't change anything if you don't want issues with JSON deserialization
                    SharedPrefs.refreshToken = rt
                    SharedPrefs.token = token
                    return token
                }
            }
        }
        return null

    }
}

/**
Interceptor that adds headers if token exists in shared prefs.

If token is invalid the request would be handled by Authenticator
 */
class TokenInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (SharedPrefs.token != null) {
            val token = SharedPrefs.token
            val authRequest = chain.request().newBuilder()
                .addHeader("Authorization", token as String)
                .build()
            return chain.proceed(authRequest)
        }
        return chain.proceed(chain.request())
    }
}