package com.vainpower.strider.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ConfigInfo::class, SettingInfo::class], version = 2, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun configInfoDao(): ConfigInfoDao
    abstract fun settingInfoDao() : SettingInfoDao


    companion object {
        @Volatile
        private var INSTANCE : MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                     MyDatabase::class.java,
                    "my_database"
                ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                return instance
            }
        }
    }
}