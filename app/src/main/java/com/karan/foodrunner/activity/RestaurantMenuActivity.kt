package com.karan.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.adapter.RestaurantMenuRecyclerAdapter
import com.karan.foodrunner.model.RestaurantMenu
import com.karan.foodrunner.util.ConnectionManager

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var recyclerRestaurantMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantMenuRecyclerAdapter
    lateinit var restaurantId:String
    lateinit var restaurantName: String
    lateinit var proceedToCartLayout:RelativeLayout
    lateinit var btnProceedToCart:Button
    lateinit var menuProgressLayout: RelativeLayout

    var restaurantMenuList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)


        proceedToCartLayout = findViewById(R.id.layoutProceedToCart)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        menuProgressLayout = findViewById(R.id.menuProgressLayout)
        toolbar = findViewById(R.id.toolbar)

        restaurantId = intent.getStringExtra("restaurantId")!!
        restaurantName = intent.getStringExtra("restaurantName")!!

        recyclerRestaurantMenu = findViewById(R.id.recyclerRestaurantMenu)
        layoutManager = LinearLayoutManager(this)

        setUpToolBar()


    }

    fun fetchData() {

        if(ConnectionManager().checkConnectivity(this))
        {
            menuProgressLayout.visibility = View.VISIBLE

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if (success) {
                            restaurantMenuList.clear()
                            val data = response.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurant = data.getJSONObject(i)
                                val menuObject = RestaurantMenu(
                                    restaurant.getString("id"),
                                    restaurant.getString("name"),
                                    restaurant.getString("cost_for_one")
                                )

                                restaurantMenuList.add(menuObject)
                                recyclerAdapter = RestaurantMenuRecyclerAdapter(
                                    this,
                                    proceedToCartLayout,
                                    btnProceedToCart,
                                    restaurantId,
                                    restaurantName,
                                    restaurantMenuList
                                )

                                recyclerRestaurantMenu.adapter = recyclerAdapter
                                recyclerRestaurantMenu.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Some Unexpected Error Occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                    menuProgressLayout.visibility = View.INVISIBLE
                }, Response.ErrorListener {
                    Toast.makeText(
                        this,
                        "Some Error occurred!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    menuProgressLayout.visibility = View.INVISIBLE
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
            val dialog = android.app.AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }

            dialog.setNegativeButton("Exit"){ _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }

    }

    override fun onBackPressed() {
        if (recyclerAdapter.getSelectedItemCount() > 0) {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { _, _ ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("Cancel") { _, _ ->

            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (recyclerAdapter.getSelectedItemCount() > 0) {
                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Alert!")
                    alterDialog.setMessage("Going back will remove everything from cart")
                    alterDialog.setPositiveButton("Okay") { _, _ ->
                        super.onBackPressed()
                    }
                    alterDialog.setNegativeButton("Cancel") { _, _ ->

                    }
                    alterDialog.show()
                }else{
                    super.onBackPressed()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setUpToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    override fun onResume() {
        if(ConnectionManager().checkConnectivity(this)){
            if(restaurantMenuList.isEmpty())
                fetchData()
        }else{
            val dialog = android.app.AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }

            dialog.setNegativeButton("Exit"){ _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
        super.onResume()
    }

}