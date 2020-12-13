package top.ilum.amenity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Response
import top.ilum.amenity.data.APIResult
import top.ilum.amenity.data.Register
import top.ilum.amenity.utils.Builder
import top.ilum.amenity.utils.Endpoints
import javax.security.auth.callback.Callback

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val request = Builder.buildService(Endpoints::class.java, this)
        val animDrawable = sign_up.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(20)
        animDrawable.setExitFadeDuration(1000)
        animDrawable.start()
        val name = findViewById<EditText>(R.id.txt_name).text
        val email = findViewById<EditText>(R.id.txt_email).text
        val password = findViewById<EditText>(R.id.txt_password).text
        val username = findViewById<EditText>(R.id.txt_username).text
        fun checker(): Boolean {
            return name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()
        }

        fun hide() {
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (keyboard.isAcceptingText) {
                keyboard.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }

        findViewById<Button>(R.id.btn_create_account).setOnClickListener {
            if (!checker()) {
                hide()
                Snackbar.make(it, "Заполните все поля", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val call = request.register(
                data = Register(
                    username = username.toString(),
                    name = name.toString(),
                    email = email.toString(),
                    password = password.toString()
                )
            )

            fun pushLogin() {
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    ).putExtra(getString(R.string.networking), false)
                )
                this.finish()
            }
            call.enqueue(object : retrofit2.Callback<APIResult> {
                override fun onFailure(call: Call<APIResult>, t: Throwable) {
                    hide()
                    Snackbar.make(it, "Что-то пошло не так.", Snackbar.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<APIResult>, response: Response<APIResult>) {
                    if (response.isSuccessful) {
                        hide()
                        Snackbar.make(it, "Вы зарегистрированы!", Snackbar.LENGTH_LONG).show()
                        pushLogin()
                    } else {
                        hide()
                        Snackbar.make(it, "Имя пользователя уже занято.", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }


            })
        }
    }

}