package com.example.notesapplication.view_models

import androidx.lifecycle.ViewModel
import com.example.notesapplication.folderoption.fragment.FolderFragment
import com.example.notesapplication.fragments.FragmentPage

class MainViewModel : ViewModel() {

    var checkOrientationIsFolder = false
    val homeFragment= FragmentPage()
    val staredFragment= FragmentPage(true)
    val folderFragment = FolderFragment()

    var currentFragment = homeFragment
}