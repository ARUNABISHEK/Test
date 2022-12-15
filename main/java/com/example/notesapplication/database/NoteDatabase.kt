package com.example.notesapplication.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.folderoption.db.FolderDao
import com.example.notesapplication.folderoption.db.model.Folder
import com.example.notesapplication.variables.DATABASE_NAME
import com.example.notesapplication.variables.INSTANCE_CREATION

@Database(entities = [Notes::class, Folder::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao() : NoteDao
    abstract fun FolderDao() : FolderDao

    companion object {

        @Volatile
        private var INSTANCE : NoteDatabase? = null

        fun getInstance(context : Context) : NoteDatabase {

            var instance = INSTANCE

            return if(instance!=null) {
                Log.i(INSTANCE_CREATION,"New Dao instance Created")
                instance
            } else {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    DATABASE_NAME
                ).build()
                Log.i(INSTANCE_CREATION,"Old Dao instance Used")
                instance
            }
        }

    }

}