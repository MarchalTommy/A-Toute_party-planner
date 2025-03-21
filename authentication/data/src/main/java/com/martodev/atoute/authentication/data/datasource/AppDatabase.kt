package com.martodev.atoute.authentication.data.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.martodev.atoute.authentication.data.model.UserEntity

/**
 * Base de données Room pour l'application
 */
@Database(
    entities = [UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Récupère le DAO pour les utilisateurs
     */
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "atoute_database"

        /**
         * Migration de la version 1 à la version 2.
         * Si un champ a été ajouté ou modifié, la migration doit être implémentée ici.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Pour conserver les données actuelles tout en passant à la version 2
                // On pourrait aussi ajouter/modifier des colonnes si nécessaire, par exemple :
                // database.execSQL("ALTER TABLE users ADD COLUMN new_column TEXT DEFAULT ''")
                
                // Cette migration ne fait rien car la structure n'a pas changé,
                // mais elle permet de passer à la version 2 sans perdre les données
            }
        }

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
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 