package com.karan.foodrunner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.karan.foodrunner.R
import com.karan.foodrunner.activity.RestaurantMenuActivity
import com.karan.foodrunner.database.RestaurantDataBase
import com.karan.foodrunner.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context,private var itemList:List<RestaurantEntity>) :RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>(){


    class FavouriteViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val txtFoodRating: TextView = view.findViewById(R.id.txtFoodRating)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgRecyclerRowProfileImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val txtFav: TextView = view.findViewById(R.id.txtFav)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = itemList[position]
        val restaurantEntity = RestaurantEntity(restaurant.id,restaurant.name,restaurant.rating,restaurant.cost_for_one,restaurant.image_url)

        holder.txtFoodName.tag = restaurant.id+""
        holder.txtFoodName.text = restaurant.name
        holder.txtFoodRating.text = restaurant.rating
        holder.txtFoodPrice.text = restaurant.cost_for_one+"/Person"

        holder.txtFav.tag = "liked"
        holder.txtFav.background = context.resources.getDrawable(R.drawable.ic_fav_filled)

        Picasso.get().load(restaurant.image_url).error(R.drawable.ic_default_image_restaurant).into(holder.imgFoodImage)


        holder.llContent.setOnClickListener{
            println(holder.txtFoodName.tag.toString())
            val intent = Intent(context as Activity, RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantId",holder.txtFoodName.tag.toString())
            intent.putExtra("restaurantName",holder.txtFoodName.text.toString())
            context.startActivity(intent)
        }

        holder.txtFav.setOnClickListener {
            if(!HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute().get()){
                val result = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 2).execute().get()

                if(result)
                {
                    Toast.makeText(context as Activity, "${restaurant.name} added to Favourite", Toast.LENGTH_SHORT).show()
                    holder.txtFav.tag = "liked"
                    holder.txtFav.background = context.resources.getDrawable(R.drawable.ic_fav_filled)
                }else{
                    Toast.makeText(context as Activity, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            } else{
                val result = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 3).execute().get()
                if(result)
                {
                    Toast.makeText(context, "${restaurant.name} removed from Favourite", Toast.LENGTH_SHORT).show()
                    holder.txtFav.tag="unliked"
                    holder.txtFav.background = context.resources.getDrawable(R.drawable.ic_fav_outline)
                } else {
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class DBAsyncTask(val context: Context, private val restaurantEntity: RestaurantEntity,private val mode:Int) : AsyncTask<Void,Void,Boolean>(){
        /*
        Mode 1 -> Check DB if the restaurant is fav or not
        Mode 2 -> Save the restaurant into DB as fav
        Mode 3 -> Remove the fav restaurant
         */

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context,RestaurantDataBase::class.java,"restaurant=db").build()

            when(mode)
            {
                1 -> {
                    //Check DB if the restaurant is fav or not
                    val restaurant:RestaurantEntity?=db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    //Save restaurant into DB as Fav
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    //Remove the fav book
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

}