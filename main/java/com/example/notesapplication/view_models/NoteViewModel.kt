package com.example.notesapplication.view_models

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import com.example.notesapplication.operations.EventCompletion
import com.example.notesapplication.SetPassword
import com.example.notesapplication.database.NoteDatabase
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.database.repository.NoteRepository
import com.example.notesapplication.variables.METHOD
import com.example.notesapplication.variables.OPERATION_COMPLETED_TAG
import kotlinx.coroutines.*

class NoteViewModel(private val repository : NoteRepository) : ViewModel(),SetPassword {

    val allNotes = repository.allNotes
    val favouriteNote = repository.favouriteNote


    private val statusMessage = MutableLiveData<EventCompletion<String>>()
    val message : LiveData<EventCompletion<String>>
        get() = statusMessage

    private fun insertNote(note : Notes) = viewModelScope.launch {
//        withContext(NonCancellable) {
            repository.insert(note)
//        }
        Log.i("save","Goto view model background")
        Log.i(OPERATION_COMPLETED_TAG,"Inserted")

        withContext(Dispatchers.Main) {
            statusMessage.value = EventCompletion("Inserted!")
        }
    }

    private fun updateNote(note : Notes) = viewModelScope.launch {
        repository.update(note)
        Log.i(OPERATION_COMPLETED_TAG,"Updated")
        withContext(Dispatchers.Main) {
            statusMessage.value = EventCompletion("Updated!")
        }
    }

    private fun deleteNote(note : Notes) = viewModelScope.launch {
        repository.delete(note)
        Log.i(OPERATION_COMPLETED_TAG,"Deleted")
        withContext(Dispatchers.Main) {
            statusMessage.value = EventCompletion("Deleted!")
        }
    }

    fun update(note : Notes) {
        val job : Job = updateNote(note)

        if(!job.isActive) {
            runBlocking {
                repository.update(note)
            }
        }
        Log.i(METHOD,"update method invoked")
    }

    fun delete(note: Notes) {
        val job : Job = deleteNote(note)

        if(!job.isActive) {
            runBlocking {
                repository.delete(note)
            }
        }
        Log.i(METHOD,"delete method invoked")
    }

    fun insert(note : Notes) {
        Log.i("save",note.toString())
        val job : Job = insertNote(note)

        if(!job.isActive) {
            runBlocking {
                repository.insert(note)
            }
        }

        Log.i(METHOD,"insert method invoked")
    }

    override fun encrypt(password: String): String {
        val key = SetPassword.getFlagNumber.value

        val charArray = password.toCharArray()
        var encryptedPassword = ""

        for(i in charArray) {
            var letter : Char = (i.toInt() + key).toChar()

            if((i in 'a'..'z') && letter > 'z' || (i in 'A'..'Z') && letter > 'Z') {
                if(letter > 'z' || letter > 'Z') {
                    letter -= 26
                }
            }

            encryptedPassword += letter
        }
        return encryptedPassword
    }

    override fun decrypt(password: String): String {

        val key = SetPassword.getFlagNumber.value
        val charArray = password.toCharArray()
        var decryptedPassword = ""

        for(i in charArray) {
            var letter : Char = (i.toInt() - key).toChar()

            if((i in 'a'..'z') && letter < 'a' || (i in 'A'..'Z') && letter < 'A') {
                if(letter < 'a' || letter < 'A') {
                    letter += 26
                }
            }

            decryptedPassword += letter
        }
        return decryptedPassword
    }

}