package com.karan.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val id:Int,
    @ColumnInfo(name="name") val name:String,
    @ColumnInfo(name="rating") val rating:String,
    @ColumnInfo(name="cost_for_one") val cost_for_one:String,
    @ColumnInfo(name="image_url") val image_url:String
)
