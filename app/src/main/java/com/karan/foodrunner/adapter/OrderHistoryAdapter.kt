package com.karan.foodrunner.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.model.CartItems
import com.karan.foodrunner.model.OrderHistoryRestaurant
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONException

class OrderHistoryAdapter(val context: Context, val restaurantOrderList: ArrayList<OrderHistoryRestaurant>) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val txtRestaurantName:TextView = view.findViewById(R.id.txtRestaurantName)
        val txtDate:TextView = view.findViewById(R.id.txtDate)
        val recyclerOrderHistory:RecyclerView = view.findViewById(R.id.recyclerOrderHistory)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_row,parent,false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantOrderList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {

        val restaurantObject = restaurantOrderList[position]
        holder.txtRestaurantName.text = restaurantObject.restaurantName

        var formatDate = restaurantObject.orderPlacedAt
        formatDate = formatDate.replace("-","/")
        formatDate = formatDate.substring(0,6) + "20"+ formatDate.substring(6,8)
        holder.txtDate.text = formatDate

        val layoutManager = LinearLayoutManager(context)
        var orderItemAdapter : CartRecyclerAdapter

        if(ConnectionManager().checkConnectivity(context))
        {
            try{
                val orderItemsPerRestaurant = ArrayList<CartItems>()
                val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_name),Context.MODE_PRIVATE)

                val userId = sharedPreferences.getString("user_id","0")

                val queue = Volley.newRequestQueue(context)
                val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                    val response = it.getJSONObject("data")
                    val success = response.getBoolean("success")

                    if(success){
                        val data = response.getJSONArray("data")
                        val jsonObjectRetrieved = data.getJSONObject(position)
                        orderItemsPerRestaurant.clear()
                        val foodOrdered  = jsonObjectRetrieved.getJSONArray("food_items")

                        for(i in 0 until foodOrdered.length()){
                            val eachFoodItem = foodOrdered.getJSONObject(i)
                            val itemObject = CartItems(
                                eachFoodItem.getString("food_item_id"),
                                eachFoodItem.getString("name"),
                                eachFoodItem.getString("cost"),"000")

                            orderItemsPerRestaurant.add(itemObject)

                        }

                        orderItemAdapter = CartRecyclerAdapter(context,orderItemsPerRestaurant)
                        holder.recyclerOrderHistory.adapter = orderItemAdapter
                        holder.recyclerOrderHistory.layoutManager = layoutManager

                    }

                },Response.ErrorListener {
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "957582bfd2ec65"
                        return headers
                }
            }
        } catch (e : JSONException){
                Toast.makeText(context, "Some Unexpected error occurred !!!", Toast.LENGTH_SHORT).show()
            }        
        }

    }

}