package com.example.notesapplication.folderoption

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplication.R
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.databinding.ActivityInsideFolderBinding
import com.example.notesapplication.fragments.FragmentPage.Companion.adapter
import com.example.notesapplication.fragments.FragmentPage.Companion.noteViewModel
import com.example.notesapplication.operations.AddNote
import com.example.notesapplication.operations.MainPage
import com.example.notesapplication.variables.TEST_TAG


class InsideFolderActivity : AppCompatActivity(),MainPage {

    private lateinit var binding : ActivityInsideFolderBinding
    private val REQUEST_CODE = 1
    private var folderId : Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_inside_folder)

        folderId = intent.extras?.get("folder_id") as Int

        binding.addFile.setOnClickListener {
            val i = Intent(this,AddNote::class.java)
            i.putExtra("folder_id",folderId)
            startActivityForResult(i,REQUEST_CODE)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        noteViewModel.allNotes.observe(this) {
            adapter.setNote(it)
            adapter.fileInFolder(folderId)
            if(!adapter.insideFolderIsNoteAvailable) {
                binding.imageView2.setImageResource(R.drawable.empty_note)
                binding.imageView2.visibility = View.VISIBLE
                binding.emptyNoteFlag.visibility = View.VISIBLE
            }
            else {
                binding.imageView2.visibility = View.GONE
                binding.emptyNoteFlag.visibility = View.GONE
            }

        }

        binding.searchView.setOnQueryTextListener(
            searchQuery()
        )

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currentNote = adapter.getNote(viewHolder.adapterPosition)
                Log.i("delnote",currentNote.toString())
                //currentNote.folder_id = folderId

//                if(currentNote.folder_id == folderId) {
                    if (currentNote.lock == null)
                        deleteDialogBox(currentNote)
                    else
                        PasswordDialogBox(currentNote)
//                }

            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity(0)

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun searchQuery(): SearchView.OnQueryTextListener? {
        return object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.i(TEST_TAG, "Query entered ($newText)")

                adapter.search(newText,folderId)

                return true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUEST_CODE && resultCode == RESULT_OK) {
            val note : Notes = data?.extras?.get("note_object") as Notes
            Log.i("save",note.toString())
            noteViewModel.insert(note)

            adapter.notifyItemInserted(adapter.itemCount)
            adapter.notifyDataSetChanged()
            Toast.makeText(this@InsideFolderActivity,"Inserted...",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteDialogBox(note : Notes) {
        Log.i("tag",note.toString())
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete :")
        builder.setMessage("Do you want to delete? ")
        builder.setCancelable(false)
        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            //note.folder_id = folderId
            noteViewModel.delete(note)
            adapter.notifyItemRemoved(note.note_id)
            Toast.makeText(this@InsideFolderActivity,"Deleted...",Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
            Toast.makeText(this@InsideFolderActivity,"Canceled...",Toast.LENGTH_SHORT).show()
            adapter.notifyDataSetChanged()
            dialog.cancel()

        }

        builder.show()
    }

    private fun PasswordDialogBox(note: Notes) {
        Log.i("tag",note.toString())
        var pass = ""
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Password : ")
        builder.setCancelable(false)
        val input = EditText(this)
        input.inputType =
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            pass = input.text.toString()
            val decrypt = noteViewModel.decrypt(note.lock.toString())
            if (pass == decrypt) {
                deleteDialogBox(note)
            }
            else {
                adapter.notifyDataSetChanged()
            }

        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ ->
            dialog.cancel()
            adapter.notifyDataSetChanged()
            Toast.makeText(this@InsideFolderActivity,"Canceled...",Toast.LENGTH_SHORT).show()}

        builder.show()
    }

}