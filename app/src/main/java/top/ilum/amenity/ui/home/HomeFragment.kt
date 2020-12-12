package top.ilum.amenity.ui.home

import SharedPrefs
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Response
import top.ilum.amenity.R
import top.ilum.amenity.data.APIResult
import top.ilum.amenity.data.Territory
import top.ilum.amenity.data.User
import top.ilum.amenity.utils.Builder
import top.ilum.amenity.utils.Endpoints
import javax.security.auth.callback.Callback
import kotlin.math.roundToInt

class HomeFragment : Fragment(), OnMapReadyCallback {

    private val positions: MutableList<LatLng> = ArrayList()
    lateinit var mapView: MapView
    lateinit var googleMap: GoogleMap
    private var source = 0
    lateinit var polygonID: String
    private var destination = 1

    private var isMapMoveable = false
    private lateinit var request: Endpoints
    private lateinit var customBottomSheetDialogFragment: CustomBottomSheetDialogFragment

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
        request = Builder.buildService(Endpoints::class.java, requireContext())
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

            frame_layout.setOnTouchListener(View.OnTouchListener { _, motionEvent ->    //Draw polygon
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
                            showBottomSheet()
                        }
                    }
                }

                return@OnTouchListener isMapMoveable
            })
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    private fun showAlertDialog() {
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle("Выберите объект:").setSingleChoiceItems(items, -1) { _, which ->
//            item = which
//        }.setPositiveButton("Ok") { _, _ -> }
//        builder.create().show()
//
//    }

    @SuppressLint("InflateParams")
    private fun askAndSend(latitude: Double, longitude: Double, tempMarker: Marker? = null) {
        val builder = AlertDialog.Builder(context).setTitle("Введите информацию об объекте:")
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.marker_dialog, null)
        tempMarker?.remove()
        builder.setView(view).setPositiveButton(
            "ОК",
            DialogInterface.OnClickListener { dialogInterface, i ->
                val titleElem = view.findViewById<EditText>(R.id.name)
                val descElem = view.findViewById<EditText>(R.id.description).text
                if (TextUtils.isEmpty(titleElem.text.toString())) {
                    dialogInterface.cancel()
                    Snackbar.make(requireView(), "Ошибка! Не задано название", Snackbar.LENGTH_LONG)
                        .setAction("Повторить") { askAndSend(latitude, longitude) }
                        .show()
                } else {
                    val call = request.postMarker(
                        top.ilum.amenity.data.Marker(
                            name = titleElem.text.toString(),
                            description = descElem.toString(),
                            longitude = longitude,
                            latitude = latitude,
                            territory = polygonID,
                            user = SharedPrefs.id as String
                        )
                    )
                    call.enqueue(object : retrofit2.Callback<APIResult> {
                        override fun onResponse(
                            call: Call<APIResult>,
                            response: Response<APIResult>
                        ) {
                            if (response.isSuccessful) {
                                Snackbar.make(
                                    requireView(),
                                    "Маркер добавлен!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<APIResult>, t: Throwable) {
                            Snackbar.make(
                                requireView(),
                                "Что-то пошло не так.",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    })

                    setMarker(LatLng(latitude, longitude), titleElem.text.toString())


                }
            }).setNegativeButton("Отмена",
            DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            })
        builder.create().show()

    }


    private fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int
    ): BitmapDescriptor? {     // Method that allows to set icons for markers
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun setMarker(
        latLng: LatLng,
        title: String
    ) {      // Method that allows to set different markers
        try {
            googleMap.addMarker(
                MarkerOptions().position(latLng).title(title)
                    .icon(
                        activity?.let {
                            bitmapDescriptorFromVector(
                                it,
                                when (customBottomSheetDialogFragment.getItem()) {
                                    0 -> R.drawable.ic_bench
                                    1 -> R.drawable.ic_fence
                                    2 -> R.drawable.ic_streetlight
                                    3 -> R.drawable.ic_tree
                                    4 -> R.drawable.ic_flower
                                    5 -> R.drawable.ic_parking
                                    else -> R.drawable.ic_custom_marker
                                }
                            )
                        }
                    )
            )
        } catch (e: Exception) {
            Log.d("Horde", e.message.toString())
        }
        if (customBottomSheetDialogFragment.getItem() == 6)
            createMarkerDescription()
    }

    private fun createMarkerDescription() {

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showBottomSheet() {
        customBottomSheetDialogFragment = CustomBottomSheetDialogFragment()
        customBottomSheetDialogFragment.show(
            childFragmentManager,
            customBottomSheetDialogFragment.tag
        )

        frame_layout.setOnTouchListener(View.OnTouchListener { _, motionEvent ->    //Set marker
            if(customBottomSheetDialogFragment.getItem() == -1) {
                showBottomSheet()
            }
            else if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val point = Point(motionEvent.x.roundToInt(), motionEvent.y.roundToInt())
                val latLng = googleMap.projection.fromScreenLocation(point)
                val latitude = latLng.latitude
                val longitude = latLng.longitude
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

    private fun drawPolygon() {     // Method that allows to draw polygons
        val polygonOptions = PolygonOptions()
        val longitude: MutableList<Double> = arrayListOf()
        val latitude: MutableList<Double> = arrayListOf()
        val call = request.postTerritory(
            Territory(
                name = "Territory",
                longitude = longitude,
                latitude = latitude,
                user = SharedPrefs.id as String
            )
        )
        call.enqueue(object : retrofit2.Callback<APIResult> {
            override fun onResponse(call: Call<APIResult>, response: Response<APIResult>) {
                if (response.isSuccessful) {
                    val result = response.body() as APIResult
                    polygonID = result.id as String
                }
            }

            override fun onFailure(call: Call<APIResult>, t: Throwable) {
                Snackbar.make(requireView(), "Что-то пошло не так.", Snackbar.LENGTH_LONG).show()
            }
        })

        for (position in positions) {
            latitude.add(position.latitude)
            longitude.add(position.longitude)
        }
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
        val call = request.getMarkers()
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