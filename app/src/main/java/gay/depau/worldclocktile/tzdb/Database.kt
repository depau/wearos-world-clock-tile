package gay.depau.worldclocktile.tzdb

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import gay.depau.worldclocktile.AppSettings
import gay.depau.worldclocktile.BuildConfig
import java.io.File

@Database(entities = [City::class], version = 1, exportSchema = false)
public abstract class TimezoneDatabase : RoomDatabase() {
    abstract fun getTimezoneDao(): TimezoneDao

    companion object {
        @Volatile
        private var INSTANCE: TimezoneDatabase? = null

        @Synchronized
        fun getInstance(context: Context): TimezoneDatabase {
            return INSTANCE ?: let {
                val settings = AppSettings(context)
                if (settings.dbVersion != BuildConfig.VERSION_CODE) {
                    val dbFile = File(context.getDatabasePath("tzdb.db").absolutePath)
                    if (dbFile.exists())
                        dbFile.delete()
                }

                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        TimezoneDatabase::class.java,
                        "tzdb.db"
                    )
                    .createFromAsset("tzdb.db")
                    .build()

                INSTANCE = instance
                settings.dbVersion = BuildConfig.VERSION_CODE
                instance
            }
        }
    }
}