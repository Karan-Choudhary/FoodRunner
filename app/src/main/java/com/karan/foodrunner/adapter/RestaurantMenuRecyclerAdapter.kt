package com.karan.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.karan.foodrunner.R
import com.karan.foodrunner.activity.CartActivity
import com.karan.foodrunner.model.RestaurantMenu

class RestaurantMenuRecyclerAdapter(
                                    val context: Context,
                                    private val proceedToCartPassed:RelativeLayout,
                                    private val btnProceedToCart:Button,
                                    private val restaurantId:String,
                                    private val restaurantName:String,
                                    private var restaurantMenu:ArrayList<RestaurantMenu>) :RecyclerView.Adapter<RestaurantMenuRecyclerAdapter.RestaurantMenuViewHolder>() {

    var itemSelectedCount:Int = 0
    lateinit var proceedToCart:RelativeLayout
    var itemsSelectedId = arrayListOf<String>()

    class RestaurantMenuViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        val txtSNo: TextView = view.findViewById(R.id.txtSNo)
        val txtItemName : TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice : TextView = view.findViewById(R.id.txtItemPrice)
        val btnAddToCart : Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_menu_single_row,parent,false)
        return RestaurantMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {
        val restaurantMenuItem = restaurantMenu[position]
        proceedToCart = proceedToCartPassed

        btnProceedToCart.setOnClickListener {
            val intent = Intent(context,CartActivity::class.java)
            intent.putExtra("restaurantId",restaurantId)
            intent.putExtra("restaurantName",restaurantName)
            intent.putExtra("selectedItemsId",itemsSelectedId)
            context.startActivity(intent)
        }

        holder.btnAddToCart.setOnClickListener {
            if(holder.btnAddToCart.text.toString() == "Remove")
            {
                itemSelectedCount--
                itemsSelectedId.remove(holder.btnAddToCart.tag.toString())
                holder.btnAddToCart.text="ADD"
                holder.btnAddToCart.setBackgroundColor(Color.rgb(244,67,54))
            } else{
                itemSelectedCount++
                itemsSelectedId.add(holder.btnAddToCart.tag.toString())
                holder.btnAddToCart.text = "Remove"
                holder.btnAddToCart.setBackgroundColor(Color.rgb(255,196,0))
            }

            if(itemSelectedCount>0){
                proceedToCart.visibility = View.VISIBLE
            }else{
                proceedToCart.visibility = View.INVISIBLE
            }
        }

        holder.btnAddToCart.tag = restaurantMenuItem.id+ ""
        holder.txtSNo.text = (position + 1).toString()
        holder.txtItemName.text = restaurantMenuItem.name
        holder.txtItemPrice.text = "Rs. ${restaurantMenuItem.cost_for_one}"
    }

    override fun getItemCount(): Int {
        return restaurantMenu.size
    }

    fun getSelectedItemCount():Int{
        return itemSelectedCount
    }

}