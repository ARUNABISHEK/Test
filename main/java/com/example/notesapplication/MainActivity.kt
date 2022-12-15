package com.example.notesapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.databinding.ActivityMainBinding
import com.example.notesapplication.folderoption.db.model.Folder
import com.example.notesapplication.folderoption.fragment.FolderFragment
import com.example.notesapplication.folderoption.fragment.FolderFragment.Companion.folderViewModel
import com.example.notesapplication.folderoption.viewModels.FolderViewModel
import com.example.notesapplication.fragments.FragmentPage
import com.example.notesapplication.fragments.FragmentPage.Companion.adapter
import com.example.notesapplication.fragments.FragmentPage.Companion.noteViewModel
import com.example.notesapplication.operations.AddNote
import com.example.notesapplication.operations.MainPage
import com.example.notesapplication.operations.NavigationBar
import com.example.notesapplication.variables.NAVIGATION_INFO
import com.example.notesapplication.variables.TEST_TAG
import com.example.notesapplication.view_models.MainViewModel
import com.google.android.material.navigation.NavigationBarView
import java.util.*

class MainActivity : FragmentActivity(),MainPage,NavigationBar {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding : ActivityMainBinding
    private val REQUEST_CODE = 1
    var isFolder = false

    override fun onRestart() {
        super.onRestart()
        binding.searchView.setQuery(null,false)
        binding.searchView.clearFocus()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBar()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        viewModel = ViewModelProvider(this@MainActivity)[MainViewModel::class.java]
        initNavigation()

        binding.searchView.setOnQueryTextListener(
            searchQuery()
        )

        binding.floatingActionButton.setOnClickListener {
            if(isFolder) {
                createNewFolder()
            }
            else {
                val intent = Intent(this@MainActivity, AddNote::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }

    }

    override fun initNavigation() {

        if (viewModel.checkOrientationIsFolder) {
            setCurrentFragment(viewModel.folderFragment)
            viewModel.checkOrientationIsFolder = true
            isFolder = true
            binding.floatingActionButton.setImageResource(R.drawable.ic_baseline_folder_24)
            binding.floatingActionButton.visibility = View.VISIBLE
            Log.i(NAVIGATION_INFO,"Folder Navigation invoked")

        }
        else {
            setCurrentFragment(viewModel.currentFragment)
            viewModel.checkOrientationIsFolder = false

            if(viewModel.currentFragment==viewModel.staredFragment){
                binding.floatingActionButton.visibility = View.GONE
            }
        }
        binding.bottomNavigation.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_SELECTED


        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            when(it.itemId){

                R.id.home-> {
                    setCurrentFragment(viewModel.homeFragment)
                    viewModel.checkOrientationIsFolder = false
                    viewModel.currentFragment = viewModel.homeFragment
                    binding.searchView.setQuery(null,false)
                    binding.searchView.clearFocus()
                    isFolder = false
                    binding.floatingActionButton.setImageResource(R.drawable.ic_baseline_add_24)
                    binding.floatingActionButton.visibility = View.VISIBLE
                    Log.i(NAVIGATION_INFO,"Home Navigation invoked")
                    binding.searchView.setOnQueryTextListener(
                        searchQuery()
                    )
                }

                R.id.stared-> {
                    setCurrentFragment(viewModel.staredFragment)
                    viewModel.checkOrientationIsFolder = false
                    viewModel.currentFragment = viewModel.staredFragment
                    binding.searchView.setQuery(null,false)
                    binding.searchView.clearFocus()
                    isFolder = false
                    binding.floatingActionButton.visibility = View.GONE
                    Log.i(NAVIGATION_INFO,"Stared Navigation invoked")

                    binding.searchView.setOnQueryTextListener(
                        searchQuery()
                    )
                }

                R.id.folder -> {
                    setCurrentFragment(viewModel.folderFragment)
                    viewModel.checkOrientationIsFolder = true
                    binding.searchView.setQuery(null,false)
                    binding.searchView.clearFocus()
                    isFolder = true
                    binding.floatingActionButton.setImageResource(R.drawable.ic_baseline_folder_24)
                    binding.floatingActionButton.visibility = View.VISIBLE
                    Log.i(NAVIGATION_INFO,"Folder Navigation invoked")

                    binding.searchView.setOnQueryTextListener(
                        searchQuery()
                    )
                }
            }
            true
        }
    }

    override fun setCurrentFragment(fragment:Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }

    override fun searchQuery(): SearchView.OnQueryTextListener? {

        return object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.i(TEST_TAG, "Query entered ($newText)")

                adapter.search(newText)


                Log.i("search",adapter.itemCount.toString())
                return true
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUEST_CODE && resultCode == RESULT_OK) {
            val note : Notes = data?.extras?.get("note_object") as Notes
            noteViewModel.insert(note)

            adapter.notifyItemInserted(adapter.itemCount)
            adapter.notifyDataSetChanged()
            Log.i("save","Note saved")
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewFolder() {
        var name = ""
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Folder Name : ")

        val input = EditText(this)
        input.inputType =
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            name = input.text.toString()
            if(name=="")
                name = "Untitled"
            folderViewModel.insert(Folder(0,name, Date().toString()))
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onBackPressed() {
        if(binding.bottomNavigation.selectedItemId == R.id.home) {
            super.onBackPressed()
            finish()
        } else {
            binding.bottomNavigation.selectedItemId = R.id.home
        }

    }

    private fun setStatusBar() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.main_status_bar)
    }

}



