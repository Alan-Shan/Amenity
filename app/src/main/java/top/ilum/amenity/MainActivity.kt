package top.ilum.amenity

import SharedPrefs
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        btnCreatePolygon.text = resources.getText(R.string.txt_choose_fragment)
//        btnCreatePolygon.setOnClickListener {
//            val fragment: HomeFragment = supportFragmentManager.findFragmentByTag("Home") as HomeFragment
//            isMapMoveable = true
//            btnCreatePolygon.visibility = View.GONE
//            positions.removeAll(positions)
//            googleMap.clear()
//
//            frame_layout.setOnTouchListener(View.OnTouchListener { _, motionEvent ->    //Draw polygon
//                if (isMapMoveable) {
//                    val point = Point(motionEvent.x.roundToInt(), motionEvent.y.roundToInt())
//                    val latLng = googleMap.projection.fromScreenLocation(point)
//                    val latitude = latLng.latitude
//                    val longitude = latLng.longitude
//
//                    when (motionEvent.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            positions.add(LatLng(latitude, longitude))
//                        }
//                        MotionEvent.ACTION_MOVE -> {
//                            positions.add(LatLng(latitude, longitude))
//                        }
//                        MotionEvent.ACTION_UP -> {
//                            isMapMoveable = false
//                            source = 0
//                            destination = 1
//                            btnCreatePolygon.visibility = View.VISIBLE
//                            drawPolygon()
//                            isMarker = true
//                            showBottomSheet()
//                        }
//                    }
//                }
//
//                return@OnTouchListener isMapMoveable
//            })
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)

        val navigationUsername =
            findViewById<TextView>(R.id.nav_username) /// Setting textviews for NavBar username/email
        val navigationEmail =
            findViewById<TextView>(R.id.nav_email) /// Setting textviews for NavBar username/email
        navigationEmail.text = SharedPrefs.email /// Setting textviews for NavBar username/email
        navigationUsername.text = SharedPrefs.name /// Setting textviews for NavBar username/email
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}