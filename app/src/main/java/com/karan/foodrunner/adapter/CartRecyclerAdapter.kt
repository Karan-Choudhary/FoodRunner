package com.karan.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karan.foodrunner.R
import com.karan.foodrunner.model.CartItems

class CartRecyclerAdapter(val context: Context, private val cartItems : ArrayList<CartItems>) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.txtOrderItem.text = cartItem.itemName
        holder.txtOrderItemPrice.text = "Rs. ${cartItem.itemPrice}"
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val txtOrderItem : TextView = view.findViewById(R.id.txtOrderItem)
        val txtOrderItemPrice:TextView = view.findViewById(R.id.txtOrderItemPrice)
    }


}