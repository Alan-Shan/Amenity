import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import top.ilum.amenity.R

object SharedPrefs {
    private var sharedPreferences: SharedPreferences? = null

    fun setup(context: Context) {

        sharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
    }

    var token: String?
        get() = Key.TOKEN.getToken()
        set(token) = Key.TOKEN.setToken(token)

    var refreshToken: String?
        get() = Key.REFRESH.getToken()
        set(token) = Key.REFRESH.setToken(token)


    private enum class Key {
        TOKEN, REFRESH;


        fun getToken(): String? =
            if (sharedPreferences!!.contains(name)) sharedPreferences!!.getString(
                name,
                ""
            ) else null

        fun setToken(value: String?) =
            value?.let { sharedPreferences!!.edit { putString(name, value) } } ?: removeToken()

        fun removeToken() = sharedPreferences!!.edit { remove(name) }
    }
}