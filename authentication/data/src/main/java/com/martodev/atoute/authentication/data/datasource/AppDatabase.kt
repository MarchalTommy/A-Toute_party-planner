package com.martodev.atoute.authentication.data.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.martodev.atoute.authentication.data.model.UserEntity

/**
 * Base de données Room pour l'application
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Récupère le DAO pour les utilisateurs
     */
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "atoute_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Récupère une instance de la base de données
         *
         * @param context Contexte Android
         * @return Instance de la base de données
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 