package com.yulianti.kodytest.data.datasource.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yulianti.kodytest.data.datasource.local.db.dao.CharacterDao
import com.yulianti.kodytest.data.datasource.local.db.entities.CharacterEntity

@Database(entities = [CharacterEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}