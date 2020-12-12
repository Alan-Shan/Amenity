package top.ilum.amenity

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val animDrawable = sign_up.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(20)
        animDrawable.setExitFadeDuration(1000)
        animDrawable.start()
        
    }
}