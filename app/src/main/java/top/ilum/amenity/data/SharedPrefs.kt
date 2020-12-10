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
        get() = Key.TOKEN.get()
        set(token) = Key.TOKEN.set(token)

    var refreshToken: String?
        get() = Key.REFRESH.get()
        set(token) = Key.REFRESH.set(token)


    var id: String?
        get() = Key.ID.get()
        set(token) = Key.ID.set(token)

    var username: String?
        get() = Key.USERNAME.get()
        set(token) = Key.USERNAME.set(token)

    var email: String?
        get() = Key.EMAIL.get()
        set(token) = Key.EMAIL.set(token)

    var name: String?
        get() = Key.NAME.get()
        set(token) = Key.NAME.set(token)


    private enum class Key {
        TOKEN, REFRESH, ID, USERNAME, EMAIL, NAME;


        fun get(): String? =
            if (sharedPreferences!!.contains(name)) sharedPreferences!!.getString(
                name,
                ""
            ) else null

        fun set(value: String?) =
            value?.let { sharedPreferences!!.edit { putString(name, value) } } ?: removeToken()

        fun removeToken() = sharedPreferences!!.edit { remove(name) }
    }
}