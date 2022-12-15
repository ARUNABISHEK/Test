package com.example.notesapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.variables.TABLE_NAME

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note : Notes)

    @Update
    suspend fun updateNote(note: Notes)

    @Delete
    suspend fun deleteNote(note: Notes)

    @Query("SELECT * FROM $TABLE_NAME ORDER BY date DESC")
    fun showAllNote(): LiveData<List<Notes>>

    @Query("SELECT * FROM $TABLE_NAME WHERE favourite=1 ORDER BY date DESC")
    fun favouriteNotes() : LiveData<List<Notes>>
}