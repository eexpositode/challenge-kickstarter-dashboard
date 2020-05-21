package com.eexposito.kickstarterdashboard.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProjectEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {

        fun build(context: Context) = Room
            .databaseBuilder(context.applicationContext, AppDatabase::class.java, "MyDatabase.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}
