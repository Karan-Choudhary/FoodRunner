package com.karan.foodrunner.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.karan.foodrunner.R
import com.karan.foodrunner.model.Food
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList:ArrayList<Food>) :RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val food = itemList[position]
        holder.txtFoodName.text = food.name
        holder.txtFoodRating.text = food.rating
        holder.txtFoodPrice.text = food.cost_for_one
//        holder.imgFoodImage.setImageResource(food.foodImage)
        Picasso.get().load(food.image_url).into(holder.imgFoodImage)
        
        holder.llContent.setOnClickListener{
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val txtFoodName:TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice:TextView = view.findViewById(R.id.txtFoodPrice)
        val txtFoodRating:TextView = view.findViewById(R.id.txtFoodPrice)
        val imgFoodImage:ImageView = view.findViewById(R.id.imgRecyclerRowProfileImage)
        val llContent:LinearLayout = view.findViewById(R.id.llContent)
    }

}