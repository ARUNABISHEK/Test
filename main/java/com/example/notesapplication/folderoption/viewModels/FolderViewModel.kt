package com.example.notesapplication.folderoption.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapplication.SetPassword
import com.example.notesapplication.folderoption.db.model.Folder
import com.example.notesapplication.folderoption.db.repository.FolderRepository
import com.example.notesapplication.operations.EventCompletion
import com.example.notesapplication.variables.METHOD
import com.example.notesapplication.variables.OPERATION_COMPLETED_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderViewModel(private val repository: FolderRepository) : ViewModel(),SetPassword {

    val allFolder = repository.allFolders
    val favouriteFolder = repository.favouriteFolders


    private val statusMessage = MutableLiveData<EventCompletion<String>>()
    val message : LiveData<EventCompletion<String>>
        get() = statusMessage

    private fun createFolder(folder: Folder) = viewModelScope.launch {
        repository.insert(folder)
        Log.i(OPERATION_COMPLETED_TAG,"Inserted")

        withContext(Dispatchers.Main) {
            statusMessage.value = EventCompletion("Folder Created!")
        }
    }

    private fun updateFolder(folder: Folder) = viewModelScope.launch {
        repository.update(folder)
        Log.i(OPERATION_COMPLETED_TAG,"Updated")
        withContext(Dispatchers.Main) {
            statusMessage.value = EventCompletion("Folder name changed!")
        }
    }

    private fun deleteFolder(folder: Folder) = viewModelScope.launch {
        repository.delete(folder)
        Log.i(OPERATION_COMPLETED_TAG,"Deleted")
        withContext(Dispatchers.Main) {
            statusMessage.value = EventCompletion("Folder Deleted!")
        }
    }

    fun update(folder: Folder) {
        updateFolder(folder)
        Log.i(METHOD,"update method invoked")
    }

    fun delete(folder: Folder) {
        deleteFolder(folder)
        Log.i(METHOD,"delete method invoked")
    }

    fun insert(folder: Folder) {
        createFolder(folder)
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