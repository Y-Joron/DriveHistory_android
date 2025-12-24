package com.joron.waffle.drivehistory.infrastructure.datasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.joron.waffle.drivehistory.infrastructure.model.database.TrackEntity
import com.joron.waffle.drivehistory.infrastructure.repository.Dao

@Database(
    entities = [
        TrackEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class DriveHistoryDatabase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        private const val DB_NAME = "drivehistory.db"
        private var instance: DriveHistoryDatabase? = null

        fun getInstance(context: Context): DriveHistoryDatabase {
            if (instance != null) {
                return instance!!
            }
            instance = Room.databaseBuilder(
                context,
                DriveHistoryDatabase::class.java,
                DB_NAME,
            ).build()
            return instance!!
        }
    }
}