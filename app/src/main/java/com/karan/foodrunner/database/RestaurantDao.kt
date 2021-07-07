package com.karan.foodrunner.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants():List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE id = :restaurantID")
    fun getRestaurantById(restaurantID:String):RestaurantEntity

}