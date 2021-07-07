package com.karan.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.adapter.HomeRecyclerAdapter
import com.karan.foodrunner.model.Restaurant
import com.karan.foodrunner.util.ConnectionManager


class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar

    val foodInfoList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        progressLayout = view.findViewById(R.id.progress_Layout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity as Context)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                   try{
                       progressLayout.visibility = View.GONE
                       val response = it.getJSONObject("data")
                       val success = response.getBoolean("success")

                       if(success){
                           val data = response.getJSONArray("data")
                           for(i in 0 until data.length())
                           {
                               val foodJsonObject = data.getJSONObject(i)
                               val foodObject = Restaurant(
                                   foodJsonObject.getString("id"),
                                   foodJsonObject.getString("name"),
                                   foodJsonObject.getString("rating"),
                                   foodJsonObject.getString("cost_for_one"),
                                   foodJsonObject.getString("image_url")
                               )
                               foodInfoList.add(foodObject)

                               recyclerAdapter = HomeRecyclerAdapter(activity as Context, foodInfoList)

                               recyclerHome.adapter = recyclerAdapter

                               recyclerHome.layoutManager = layoutManager

                           }
                       } else{
                           Toast.makeText(activity as Context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                       }
                   }catch (e:Exception)
                   {
                       Toast.makeText(activity as Context, "Some Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
                   }

                }, Response.ErrorListener {
                    if(activity!=null) {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occurred, Please try again Later(VolleyError)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "957582bfd2ec65"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){ _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }
}