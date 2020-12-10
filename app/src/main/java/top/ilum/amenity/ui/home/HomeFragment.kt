package top.ilum.amenity.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.android.synthetic.main.fragment_home.*
import top.ilum.amenity.R
import kotlin.math.roundToInt


class HomeFragment : Fragment(), OnMapReadyCallback {

    private val positions: MutableList<LatLng> = ArrayList()
    lateinit var mapView: MapView
    lateinit var googleMap: GoogleMap
    private var source = 0
    private var destination = 1

    private var isMapMoveable = false
    private var screenLeave = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        activity?.let { ButterKnife.bind(it) }
        mapView = v.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapView.getMapAsync(this)
        return v
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnCreatePolygon.text = getString(R.string.txt_choose_fragment)
        btnCreatePolygon.setOnClickListener {
            isMapMoveable = true
            btnCreatePolygon.visibility = View.GONE
            positions.removeAll(positions)
            googleMap.clear()
        }
        frame_layout.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            if (isMapMoveable) {
                val point = Point(motionEvent.x.roundToInt(), motionEvent.y.roundToInt())
                val latLng = googleMap.projection.fromScreenLocation(point)
                val latitude = latLng.latitude
                val longitude = latLng.longitude

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        positions.add(LatLng(latitude, longitude))
                        screenLeave = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        positions.add(LatLng(latitude, longitude))
                        screenLeave = false
                    }
                    MotionEvent.ACTION_UP -> {
                        isMapMoveable = false
                        source = 0
                        destination = 1
                        btnCreatePolygon.visibility = View.VISIBLE
                        drawPolygon()
                    }
                }
            }

            return@OnTouchListener isMapMoveable
        })
    }

    private fun drawPolygon() {
        val polygonOptions = PolygonOptions()
        polygonOptions.addAll(positions)
        polygonOptions.strokeColor(Color.RED)
            .fillColor(resources.getColor(R.color.polygonColor))
        googleMap.addPolygon(polygonOptions)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0 as GoogleMap
    }

}