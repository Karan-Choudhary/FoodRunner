package com.karan.foodrunner.model

data class OrderHistoryRestaurant(
    val orderId:String,
    val restaurantName:String,
    val totalCost:String,
    val orderPlacedAt:String
)
