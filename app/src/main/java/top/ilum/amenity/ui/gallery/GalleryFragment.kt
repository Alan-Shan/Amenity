package top.ilum.amenity.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import top.ilum.amenity.R
import top.ilum.amenity.data.User
import top.ilum.amenity.utils.Builder
import top.ilum.amenity.utils.Endpoints

class GalleryFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)

        val request = Builder.buildService(Endpoints::class.java)
        val call = request.getUsers()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    Log.e("rest", response.toString())
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
            }
        })
        textView.text = "as"
        return root
    }
}