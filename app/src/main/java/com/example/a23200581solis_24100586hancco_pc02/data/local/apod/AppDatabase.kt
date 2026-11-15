package com.example.a23200581solis_24100586hancco_pc02.data.local.apod

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.neo.AsteroidDao
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.neo.AsteroidEntity

@Database(
    entities = [
        ApodEntity::class,
        AsteroidEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun apodDao(): ApodDao

    abstract fun asteroidDao(): AsteroidDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `asteroids` (
                        `asteroidId` TEXT NOT NULL,
                        `approachDate` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `isPotentiallyHazardous` INTEGER NOT NULL,
                        `diameterMinMeters` REAL,
                        `diameterMaxMeters` REAL,
                        `velocityKmh` REAL,
                        `distanceKm` REAL,
                        `isFavorite` INTEGER NOT NULL,
                        PRIMARY KEY(`asteroidId`, `approachDate`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diario_espacial.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}