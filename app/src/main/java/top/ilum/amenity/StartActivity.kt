package top.ilum.amenity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import top.ilum.amenity.data.User
import top.ilum.amenity.utils.Builder
import top.ilum.amenity.utils.Endpoints

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun pushLogin(net: Boolean = false) {
            startActivity(Intent(this, LoginActivity::class.java).putExtra(getString(R.string.networking), net))
            this.finish()
        }

        SharedPrefs.setup(this) // Initialize SP object
        if (SharedPrefs.refreshToken != null) {

            val request = Builder.buildService(Endpoints::class.java, this)
            val call = request.getUser(SharedPrefs.id as String)
            //Updating user data on each login
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body() as User
                        SharedPrefs.email = user.email
                        SharedPrefs.name = user.name
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    pushLogin(true)
                }
            })

            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        } else {
            pushLogin()
        }

    }


}