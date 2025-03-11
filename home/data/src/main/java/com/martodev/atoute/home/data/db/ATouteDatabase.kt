package com.martodev.atoute.home.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.martodev.atoute.home.data.dao.ParticipantDao
import com.martodev.atoute.home.data.dao.PartyDao
import com.martodev.atoute.home.data.dao.ToBuyDao
import com.martodev.atoute.home.data.dao.TodoDao
import com.martodev.atoute.home.data.entity.ParticipantEntity
import com.martodev.atoute.home.data.entity.PartyEntity
import com.martodev.atoute.home.data.entity.ToBuyEntity
import com.martodev.atoute.home.data.entity.TodoEntity
import com.martodev.atoute.home.data.util.DateConverters

/**
 * Base de données Room pour l'application A-Toute
 */
@Database(
    entities = [
        PartyEntity::class,
        ParticipantEntity::class,
        TodoEntity::class,
        ToBuyEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(DateConverters::class)
abstract class ATouteDatabase : RoomDatabase() {
    
    abstract fun partyDao(): PartyDao
    abstract fun todoDao(): TodoDao
    abstract fun toBuyDao(): ToBuyDao
    abstract fun participantDao(): ParticipantDao
    
    companion object {
        private const val DATABASE_NAME = "atoute_db"
        
        /**
         * Migration de la version 1 à 2 :
         * Ajout des colonnes todoCount et completedTodoCount à la table parties
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ajouter les colonnes todoCount et completedTodoCount à la table parties
                database.execSQL("ALTER TABLE parties ADD COLUMN todoCount INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE parties ADD COLUMN completedTodoCount INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        @Volatile
        private var instance: ATouteDatabase? = null
        
        fun getInstance(context: Context): ATouteDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        
        private fun buildDatabase(context: Context): ATouteDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ATouteDatabase::class.java,
                DATABASE_NAME
            )
            .addMigrations(MIGRATION_1_2)
            .build()
        }
    }
} 