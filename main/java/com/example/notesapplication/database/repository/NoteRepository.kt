package com.example.notesapplication.database.repository

import android.util.Log
import com.example.notesapplication.database.NoteDao
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.variables.REPOSITORY_CHECK

class NoteRepository(private val dao: NoteDao) {

    val allNotes = dao.showAllNote()
    val favouriteNote = dao.favouriteNotes()

    suspend fun insert(note : Notes) {
        Log.i("save","Goto view Repository")
        dao.insertNote(note)
        Log.i(REPOSITORY_CHECK, "Insert repository method invoked")


    }

    suspend fun update(note : Notes) {
        dao.updateNote(note)
        Log.i(REPOSITORY_CHECK, "Update repository method invoked")
    }

    suspend fun delete(note : Notes) {
        dao.deleteNote(note)
        Log.i(REPOSITORY_CHECK, "Delete repository method invoked")
    }



}

