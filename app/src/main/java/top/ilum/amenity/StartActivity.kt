package top.ilum.amenity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        SharedPrefs.setup(this) // Initialize SP object

        if (SharedPrefs.refreshToken != null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()

        }
    }


}