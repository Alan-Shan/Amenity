package top.ilum.amenity.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
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
    private var isMarker = false

    private val items = arrayOf("Скамья", "Ограждение", "Фонарь", "Дерево", "Клумба", "Паркинг", "Создать свой объект")
    private var item = 0
    private lateinit var polygon: Polygon

//    private val mBottomSheetBehavior: BottomSheetBehavior<*>? = null

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

            frame_layout.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
                if (isMapMoveable) {
                    val point = Point(motionEvent.x.roundToInt(), motionEvent.y.roundToInt())
                    val latLng = googleMap.projection.fromScreenLocation(point)
                    val latitude = latLng.latitude
                    val longitude = latLng.longitude

                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            positions.add(LatLng(latitude, longitude))
                        }
                        MotionEvent.ACTION_MOVE -> {
                            positions.add(LatLng(latitude, longitude))
                        }
                        MotionEvent.ACTION_UP -> {
                            isMapMoveable = false
                            source = 0
                            destination = 1
                            btnCreatePolygon.visibility = View.VISIBLE
                            drawPolygon()
                            isMarker = true
                            showAlertDialog()
//                        showBottomSheet()
                        }
                    }
                }

                return@OnTouchListener isMapMoveable
            })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Выберите объект:").setSingleChoiceItems(items, -1) { _, which ->
            item = which
        }.setPositiveButton("Ok") { _, _ -> }
        builder.create().show()
            frame_layout.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    val point = Point(motionEvent.x.roundToInt(), motionEvent.y.roundToInt())
                    val latLng = googleMap.projection.fromScreenLocation(point)
                    val latitude = latLng.latitude
                    val longitude = latLng.longitude
                    isMarker = false
                    if (PolyUtil.containsLocation(latLng, positions, true)) {
                        val tempMarker: Marker = googleMap.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    latitude,
                                    longitude
                                )
                            ).icon(BitmapDescriptorFactory.defaultMarker())
                        )
                        askAndSend(latitude, longitude, tempMarker)
                    }
                }

                return@OnTouchListener isMapMoveable
            })
    }

    @SuppressLint("InflateParams")
    private fun askAndSend(latitude: Double, longitude: Double, tempMarker: Marker) {
        val builder = AlertDialog.Builder(context).setTitle("Введите информацию об объекте:")
        val inflater = requireActivity().layoutInflater

        tempMarker.remove()
        builder.setView(inflater.inflate(R.layout.marker_dialog, null)).setPositiveButton("ОК",
            DialogInterface.OnClickListener { dialogInterface, i ->
                setMarker(LatLng(latitude, longitude), "title")
            }).setNegativeButton("Отмена",
            DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            })
        builder.create().show()

    }

  
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun setMarker(latLng: LatLng, title: String) {
        try {
            googleMap.addMarker(
                MarkerOptions().position(latLng).title(title)
                    .icon(
                        activity?.let {
                            bitmapDescriptorFromVector(
                                it,
                                when (item) {
                                    0 -> R.drawable.ic_bench
                                    1 -> R.drawable.ic_fence
                                    2 -> R.drawable.ic_streetlight
                                    3 -> R.drawable.ic_tree
                            4 -> R.drawable.ic_flower
                            5 -> R.drawable.ic_parking
                            else -> R.drawable.ic_custom_marker
                        }) }
                    )
            )
        }catch (e: Exception){
            Log.d("Horde", e.message.toString())
        }
        if (item == 6)
            createMarkerDescription()
    }

    private fun createMarkerDescription() {

    }

//    private fun showBottomSheet() {
//        CustomBottomSheetDialogFragment().apply {
//            if (isAdded)
//                show(childFragmentManager, CustomBottomSheetDialogFragment.TAG)
//        }
//    }

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
        // Create a LatLngBounds that includes Russia
        val russia = LatLngBounds(
            LatLng(51.077775, 31.681419),
            LatLng(76.760435, 166.123767)
        )
        // Constrain the camera target to Russia.
        googleMap.setLatLngBoundsForCameraTarget(russia)
    }

}