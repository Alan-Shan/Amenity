package top.ilum.amenity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val animDrawable = login_layout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(20)
        animDrawable.setExitFadeDuration(1000)
        animDrawable.start()

        val message = intent.getBooleanExtra(
            getString(R.string.networking),
            false
        ) //Check if no network message was passed and notify a user
        if (message) {
            Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.unavailable),
                Snackbar.LENGTH_LONG
            ).show()
            no_network_image.visibility = View.VISIBLE
            no_network_message.visibility = View.VISIBLE
            materialButton.visibility = View.INVISIBLE
        }

        val loginButton = findViewById<Button>(R.id.login)
        val username = findViewById<EditText>(R.id.nav_username)
        val password = findViewById<EditText>(R.id.password)
        val baseURL = getString(R.string.base_url)

        materialButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        fun validate(): Boolean {
            return username.text.toString() != "" && password.text.toString() != ""
        }

        loginButton.setOnClickListener {
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            fun hide() {
                if (keyboard.isAcceptingText) {
                    keyboard.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                }
            }

            if (validate()) {
                val auth = Credentials.basic(username.text.toString(), password.text.toString())
                val client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build()
                val request = Request.Builder()
                    .url("$baseURL/auth")
                    .addHeader(getString(R.string.auth), auth)
                    .build()

                fun proceed(case: Int) {

                    when (case) {
                        0 -> {
                            hide()
                            Snackbar.make(
                                it,
                                getString(R.string.access_denied),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        1 -> {
                            startActivity(Intent(this, MainActivity::class.java))
                            this.finish()
                        }
                        else -> {
                            hide()
                            Snackbar.make(it, getString(R.string.unavailable), Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
                }

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        proceed(2)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use { r ->
                            if (!response.isSuccessful && response.code == 401) {
                                proceed(0)
                            } else {
                                try {

                                    val tokenBody = response.body!!.string().toString()
                                    val tokenJson = JSONObject(tokenBody)
                                    val token = tokenJson.getString("token")
                                    SharedPrefs.refreshToken = tokenJson.getString("refresh_token")
                                    SharedPrefs.token = token
                                    SharedPrefs.id = tokenJson.getString("id")
                                    SharedPrefs.name = tokenJson.getString("name")
                                    SharedPrefs.username = tokenJson.getString("username")
                                    SharedPrefs.email = tokenJson.getString("email")
                                    proceed(1)
                                } catch (e: Exception) {
                                    proceed(0)
                                }
                            }
                        }
                    }
                })


            } else {
                hide()
                Snackbar.make(it, getString(R.string.fill_auth), Snackbar.LENGTH_LONG).show()
            }


        }
    }

    override fun onBackPressed() { // Intercept back button click
        moveTaskToBack(true);
        finish() //  Close app if user is going back from the login screen
    }
}
