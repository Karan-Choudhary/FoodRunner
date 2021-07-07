package com.karan.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntity::class],version = 1)
abstract class RestaurantDataBase:RoomDatabase() {

    abstract fun restaurantDao() : RestaurantDao

}