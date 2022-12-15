package com.example.notesapplication.operations

import android.widget.SearchView
import androidx.fragment.app.Fragment

interface MainPage {
        fun searchQuery() : SearchView.OnQueryTextListener?
}

interface NavigationBar {
        fun initNavigation()
        fun setCurrentFragment(fragment: Fragment)
}