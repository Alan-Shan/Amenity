package top.ilum.amenity.ui.settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import top.ilum.amenity.R
import top.ilum.amenity.data.APIResult
import top.ilum.amenity.data.Communities
import top.ilum.amenity.utils.Builder
import top.ilum.amenity.utils.Endpoints

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val request = Builder.buildService(Endpoints::class.java, requireContext())
        val communitiesCall = request.getCommunities()
        val communitesInternal: ArrayList<Communities> = arrayListOf()
        val communities = arrayListOf<String>()
        communities.add("Выберите дом")
        communitiesCall.enqueue(object : Callback<List<Communities>> {
            override fun onResponse(
                call: Call<List<Communities>>,
                response: Response<List<Communities>>
            ) {
                if (response.isSuccessful) { // Get communities (Also handles empty response cases so null check is of no effect)
                    for (comm in response.body()!!) {
                        communities.add(comm.name)
                        communitesInternal.add(Communities(id = comm.id, name = comm.name))
                    }
                    communities.add("Создать новый")
                }
            }

            override fun onFailure(call: Call<List<Communities>>, t: Throwable) {
                Snackbar.make(requireView(), "Что-то пошло не так.", Snackbar.LENGTH_LONG).show()
            }
        })
        val changeCommunity = root.findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            communities
        )
        changeCommunity.adapter = adapter

        changeCommunity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Snackbar.make(requireView(), "Выберите дом", Snackbar.LENGTH_LONG)
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                    }
                    communities.size - 1 -> {
                        val builder =
                            AlertDialog.Builder(context).setTitle("Введите название:")
                        val dialogInflater = requireActivity().layoutInflater
                        val view = dialogInflater.inflate(R.layout.community_dialog, null)
                        builder.setView(view).setPositiveButton(
                            "ОК", DialogInterface.OnClickListener { dialogInterface, i ->
                                val titleElem =
                                    view.findViewById<EditText>(R.id.name).text.toString()
                                if (TextUtils.isEmpty(titleElem)) {
                                    dialogInterface.cancel()
                                    Snackbar.make(
                                        requireView(),
                                        "Ошибка! Не задано название",
                                        Snackbar.LENGTH_LONG
                                    )
                                        .show()
                                } else {
                                    val call =
                                        request.postCommunities(Communities(name = titleElem))
                                    call.enqueue(object : Callback<APIResult> {
                                        override fun onResponse(
                                            call: Call<APIResult>,
                                            response: Response<APIResult>
                                        ) {
                                            communities.remove("Создать новый")
                                            communitesInternal.add(
                                                Communities(
                                                    id = response.body()?.id,
                                                    name = titleElem
                                                )
                                            )
                                            communities.add(titleElem)
                                            communities.add("Создать новый")
                                            changeCommunity.setSelection(communities.size - 2, true)
                                            SharedPrefs.room =
                                                communitesInternal[communitesInternal.size - 1].id
                                        }

                                        override fun onFailure(
                                            call: Call<APIResult>,
                                            t: Throwable
                                        ) {
                                            Snackbar.make(
                                                requireView(),
                                                "Что-то пошло не так.",
                                                Snackbar.LENGTH_LONG
                                            ).show()
                                        }
                                    })
                                }
                            }).setNegativeButton("Отмена",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.cancel()
                                changeCommunity.setSelection(communities.size - 2)
                            })
                        builder.create().show()

                    }
                    else -> {
                        SharedPrefs.room =
                            communitesInternal[changeCommunity.selectedItemPosition - 1].id
                    }
                }
            }


        }


        return root
    }
}