package com.karan.foodrunner.fragment

import android.content.Context
import android.content.RestrictionEntry
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.karan.foodrunner.R
import com.karan.foodrunner.adapter.FavouriteRecyclerAdapter
import com.karan.foodrunner.database.RestaurantDataBase
import com.karan.foodrunner.database.RestaurantEntity

class FavouriteRestaurantsFragment : Fragment() {

    private lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var whenNull:RelativeLayout


    private var dbRestaurantList = listOf<RestaurantEntity>()

       override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)

           recyclerFavourite =view.findViewById(R.id.recyclerFavourite)
           progressLayout = view.findViewById(R.id.progress_Layout)
           progressBar = view.findViewById(R.id.progressBar)

           progressLayout.visibility = View.VISIBLE
           progressBar.visibility = View.VISIBLE

           whenNull = view.findViewById(R.id.whenNull)


           layoutManager = LinearLayoutManager(activity as Context)

           dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()

           if(activity!=null)
           {
               progressLayout.visibility = View.GONE
               progressBar.visibility = View.GONE

               recyclerAdapter = FavouriteRecyclerAdapter(activity as Context,dbRestaurantList)
               recyclerFavourite.adapter = recyclerAdapter
               recyclerFavourite.layoutManager = layoutManager

           }

           if(dbRestaurantList.isEmpty())
           {
               whenNull.visibility = View.VISIBLE
           }else{
               whenNull.visibility = View.INVISIBLE
           }

        return view
    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDataBase::class.java,"restaurant-db").build()

            return db.restaurantDao().getAllRestaurants()
        }

    }
}