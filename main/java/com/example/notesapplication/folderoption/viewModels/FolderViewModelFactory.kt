package com.example.notesapplication.folderoption.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notesapplication.folderoption.db.repository.FolderRepository
import java.lang.IllegalArgumentException

class FolderViewModelFactory(private val repository: FolderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FolderViewModel::class.java)) {
            return FolderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}