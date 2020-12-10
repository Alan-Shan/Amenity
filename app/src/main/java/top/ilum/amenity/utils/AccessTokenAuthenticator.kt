package top.ilum.amenity.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import okhttp3.*
import org.json.JSONObject
import top.ilum.amenity.LoginActivity
import top.ilum.amenity.StartActivity

/**
Token refresher on 401
 */
class AccessTokenAuthenticator(context: Context) : Authenticator {
    private val appContext = context

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
                    rt = tokenJson.getString("refresh_token")
                    // Cascade used on a reason, don't change anything if you don't want issues with JSON deserialization
                    SharedPrefs.refreshToken = rt
                    SharedPrefs.token = token
                    return token
                } else {
                    startActivity(appContext, Intent(appContext, LoginActivity::class.java), null)
                }
            }
            startActivity(
                StartActivity::getApplicationContext as Context,
                Intent(StartActivity::getApplicationContext as Context, LoginActivity::class.java),
                null
            )
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