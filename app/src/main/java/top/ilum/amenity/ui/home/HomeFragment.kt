package top.ilum.amenity.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.android.synthetic.main.fragment_home.*
import top.ilum.amenity.R

class HomeFragment : Fragment(), OnMapReadyCallback {

    lateinit var mapView: MapView
    lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        mapView = v.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception){
            e.printStackTrace()
        }
        mapView.getMapAsync(this)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnCreatePolygon.text = "Выбрать фрагмент"
        btnCreatePolygon.setOnClickListener {
            btnCreatePolygon.text = "Выберите точки на карте"
            createPolygon()
        }
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

    private fun createPolygon() {
        val positions = ArrayList<LatLng>()
        googleMap.setOnMapClickListener { latLng -> positions += latLng
            btnCreatePolygon.text = "Готово"
            googleMap.addCircle(
                CircleOptions().center(latLng).radius(1.0).strokeColor(Color.RED).fillColor(
                    Color.RED))
        }
        btnCreatePolygon.setOnClickListener {
            val polygonOptions = PolygonOptions()
            positions.forEach {
                polygonOptions.add(it)
            }
            polygonOptions.strokeColor(Color.RED).fillColor(Color.parseColor("#50FFB7B7"))
            googleMap.addPolygon(polygonOptions)
            btnCreatePolygon.text = "Выбрать фрагмент"
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0 as GoogleMap
    }

}