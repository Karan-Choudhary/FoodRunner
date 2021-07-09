package com.karan.foodrunner.activity

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Placeholder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.adapter.CartRecyclerAdapter
import com.karan.foodrunner.model.CartItems
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var txtOrderingFrom: TextView
    lateinit var btnPlaceOrder: Button

    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManger: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var restaurantId:String
    lateinit var restaurantName:String
    private lateinit var selectedItemsId:ArrayList<String>
    lateinit var linearLayout: LinearLayout
    lateinit var cartProgressLayout: RelativeLayout
    lateinit var relativeLayoutProceedToCart : RelativeLayout

    var totalAmount = 0
    var cartListItems = arrayListOf<CartItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        txtOrderingFrom = findViewById(R.id.txtOrderingFrom)
        linearLayout = findViewById(R.id.linearLayout)
        toolbar = findViewById(R.id.toolbar)
        cartProgressLayout = findViewById(R.id.cartProgressLayout)
        relativeLayoutProceedToCart = findViewById(R.id.relativeLayoutProceedToCart)


        restaurantId = intent.getStringExtra("restaurantId").toString()
        restaurantName = intent.getStringExtra("restaurantName").toString()
        selectedItemsId = intent.getStringArrayListExtra("selectedItemsId") as ArrayList<String>
        txtOrderingFrom.text = restaurantName

        recyclerCart = findViewById(R.id.recyclerCart)
        layoutManger = LinearLayoutManager(this)

        setUpToolbar()


        btnPlaceOrder.setOnClickListener {

            val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),MODE_PRIVATE)

            if(ConnectionManager().checkConnectivity(this))
            {
                cartProgressLayout.visibility = View.VISIBLE
                try{
                    val foodArray = JSONArray()
                    for(foodItem in selectedItemsId){
                        val singleItemObject = JSONObject()
                        singleItemObject.put("food_item_id",foodItem)
                        foodArray.put(singleItemObject)
                    }

                    val sendOrder = JSONObject()
                    sendOrder.put("user_id",sharedPreferences.getString("user_id","0"))
                    sendOrder.put("restaurant_id",restaurantId)
                    sendOrder.put("total_cost",totalAmount)
                    sendOrder.put("food",foodArray)
                    Log.e("send object is ", sendOrder.toString())
                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                    val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,url,sendOrder,Response.Listener {

                      val response = it.getJSONObject("data")
                      val success = response.getBoolean("success")

                      if(success)
                      {
                          Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()
                          createNotification()
                          val intent = Intent(this,OrderPlacedActivity::class.java)
                          startActivity(intent)
                          finishAffinity()
                      }else{
                          val responseMessageServer = response.getString("errorMessage")
                          Toast.makeText(this, responseMessageServer, Toast.LENGTH_SHORT).show()
                      }
                        cartProgressLayout.visibility = View.INVISIBLE
                    },Response.ErrorListener {
                        Toast.makeText(this, "Some Error Occurred!!!", Toast.LENGTH_SHORT).show()

                    }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String,String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "957582bfd2ec65"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                } catch (e : JSONException){
                    Toast.makeText(this, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
                }

            }else
            {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is Not Found")
                dialog.setPositiveButton("Open Settings"){_,_ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    this?.finish()
                }
                dialog.setNegativeButton("Exit"){_,_ ->
                    ActivityCompat.finishAffinity(this as Activity)
                }
                dialog.create()
                dialog.show()
            }

        }

    }

    fun fetchData()
    {
        if(ConnectionManager().checkConnectivity(this))
        {
            cartProgressLayout.visibility = View.VISIBLE
            try {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                    val response = it.getJSONObject("data")
                    val success = response.getBoolean("success")

                    if(success)
                    {
                        val data = response.getJSONArray("data")
                        cartListItems.clear()
                        totalAmount = 0

                        for(i in 0 until data.length())
                        {
                            val cartItem = data.getJSONObject(i)
                            if(selectedItemsId.contains(cartItem.getString("id")))
                            {
                                val menuObject = CartItems(
                                    cartItem.getString("id"),
                                    cartItem.getString("name"),
                                    cartItem.getString("cost_for_one"),
                                    cartItem.getString("restaurant_id"))

                                totalAmount += cartItem.getString("cost_for_one").toString().toInt()
                                cartListItems.add(menuObject)
                            }
                            recyclerAdapter = CartRecyclerAdapter(this,cartListItems)
                            recyclerCart.adapter = recyclerAdapter
                            recyclerCart.layoutManager = layoutManger
                        }
                        btnPlaceOrder.text = "Place Order(Total: Rs. ${totalAmount})"
                    }
                    cartProgressLayout.visibility = View.INVISIBLE
                },Response.ErrorListener {
                    Toast.makeText(this, "SOME Error Occurred !!!", Toast.LENGTH_SHORT).show()
                    cartProgressLayout.visibility = View.INVISIBLE
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "957582bfd2ec65"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }catch (e :JSONException){
                Toast.makeText(this, "Some Unexpected Error Occurred !!!!", Toast.LENGTH_SHORT).show()
            }
        }else{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is Not Found")
            dialog.setPositiveButton("Open Settings"){_,_ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                this?.finish()
            }
            dialog.setNegativeButton("Exit"){_,_ ->
                ActivityCompat.finishAffinity(this as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onResume() {
        if (ConnectionManager().checkConnectivity(this)) {
            fetchData()
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
        super.onResume()    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


    fun createNotification() {
        val notificationId = 1;
        val channelId = "personal_notification"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder.setSmallIcon(R.drawable.logo)
        notificationBuilder.setContentTitle("Order Placed")
        notificationBuilder.setContentText("Your order has been successfully placed!")
        notificationBuilder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("Ordered from ${restaurantName}. Please pay Rs.$totalAmount")
        )

        notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Order Placed"
            val description = "Your order has been successfully placed!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, name, importance)
            notificationChannel.description = description

            val notificationManager =
                (getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}