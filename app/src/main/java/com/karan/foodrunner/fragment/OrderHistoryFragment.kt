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
import com.karan.foodrunner.adapter.OrderHistoryAdapter
import com.karan.foodrunner.model.OrderHistoryRestaurant
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderRestaurant: RecyclerView
    lateinit var layoutManager1: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryAdapter
    lateinit var orderHistoryLayout: RelativeLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var noOrders: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerOrderRestaurant = view.findViewById(R.id.recyclerOrderRestaurant)
        orderHistoryLayout = view.findViewById(R.id.orderHistoryLayout)
        toolbar = view.findViewById(R.id.toolbar)
        noOrders = view.findViewById(R.id.noOrders)

        return view
    }

    fun setItemsForEachRestaurant() {
        layoutManager1 = LinearLayoutManager(context)
        val orderedRestaurantList = ArrayList<OrderHistoryRestaurant>()

        val sharedPreferences = context?.getSharedPreferences(
            context?.getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        val userId = sharedPreferences?.getString("user_id", "0")

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            orderHistoryLayout.visibility = View.VISIBLE

            try {
                val queue = Volley.newRequestQueue(context)
                val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

                val jsonObjectRequest =
                    object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if(success)
                        {
                            val data = response.getJSONArray("data")
                            if(data.length()==0)
                            {
                                Toast.makeText(context, "No order placed yet", Toast.LENGTH_SHORT).show()
                                noOrders.visibility = View.VISIBLE
                            }else{
                                noOrders.visibility = View.INVISIBLE

                                for(i in 0 until data.length())
                                {
                                    val restaurantItem = data.getJSONObject(i)
                                    val restaurantObject = OrderHistoryRestaurant(
                                        restaurantItem.getString("order_id"),
                                        restaurantItem.getString("restaurant_name"),
                                        restaurantItem.getString("total_cost"),
                                        restaurantItem.getString("order_placed_at").substring(0,10)
                                    )
                                    orderedRestaurantList.add(restaurantObject)
                                    recyclerAdapter = OrderHistoryAdapter(activity as Context,orderedRestaurantList)
                                    recyclerOrderRestaurant.adapter = recyclerAdapter
                                    recyclerOrderRestaurant.layoutManager = layoutManager1
                                }
                            }
                        }else{
                            Toast.makeText(activity as Context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                        }
                        orderHistoryLayout.visibility = View.INVISIBLE
                    }, Response.ErrorListener {
                        orderHistoryLayout.visibility = View.INVISIBLE
                        Toast.makeText(activity as Context, "Some Unexpected Error Occurred !!", Toast.LENGTH_SHORT).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "957582bfd2ec65"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
            } catch (e:JSONException){
                Toast.makeText(activity as Context, "Some Unexpected error occurred !!!!!", Toast.LENGTH_SHORT).show()
            }

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
    }

    override fun onResume() {
        if(ConnectionManager().checkConnectivity(activity as Context))
        {
            setItemsForEachRestaurant()
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
        super.onResume()
    }
}