package com.dilarakiraz.upschoolcapstoneproject.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductEntity

/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class ProductRoomDB : RoomDatabase(){

    abstract fun productsDao(): ProductDao
}