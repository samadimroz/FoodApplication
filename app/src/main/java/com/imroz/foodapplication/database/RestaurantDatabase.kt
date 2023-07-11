package com.imroz.foodapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntities::class, OrderEntity::class], version = 1, exportSchema = false)
abstract class RestaurantDatabase : RoomDatabase(){
    abstract fun restaurantDao(): RestaurantDao
    abstract fun orderDao(): OrderDao
}