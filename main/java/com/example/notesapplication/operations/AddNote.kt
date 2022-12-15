package com.example.notesapplication.operations

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.notesapplication.R
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.databinding.AddNoteBinding
import com.example.notesapplication.fragments.FragmentPage.Companion.adapter
import com.example.notesapplication.fragments.FragmentPage.Companion.noteViewModel
import com.example.notesapplication.variables.COLOR
import com.example.notesapplication.variables.OPERATION_COMPLETED_TAG
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class AddNote : AppCompatActivity() {

    private lateinit var binding : AddNoteBinding
    private lateinit var currentNote : Notes
    private var isUpdated = false
    private var favourite = false
    private var flagStar = false
    private var lock : String?= null
    private var lockFlag = false
    private var folderId = -1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.add_note)

        binding.noteFrame.setBackgroundColor(COLOR[(0 until COLOR.size).random()])
        binding.dateTextView.text = Date().toString() //getCurrentDate()

        //Intent Get Object
        if(intent.extras?.get("isUpdate") == true) {
            currentNote = intent.extras?.get("noteObject") as Notes
            isUpdated = intent.extras?.get("isUpdate") as Boolean
            binding.titleTextView.text = currentNote.title?.toEditable()
            binding.noteTextView.text = currentNote.note?.toEditable()
            initSymbols(currentNote)
        }

        if(intent.extras?.get("folder_id")!=null) {
            folderId = intent.extras?.get("folder_id") as Int
        }
        //Insert
        binding.SaveImageButton.setOnClickListener {
            if(isUpdated)
                update()
            else
                save()
        }

        binding.backArrowButton.setOnClickListener {
            if(isUpdated &&
                binding.titleTextView.text.toString() == currentNote.title.toString() &&
                binding.noteTextView.text.toString() == currentNote.note.toString() &&
                currentNote.favourite == favourite &&
                currentNote.lock == lock)

                goToCalledActivity()
            else
                back(isUpdated)
        }

        binding.favouriteImageButton.setOnClickListener {
            if(!flagStar) {
                favourite = true
                flagStar = true
                binding.favouriteImageButton.setImageResource(R.drawable.ic_baseline_star_24)
            } else {
                favourite = false
                flagStar = false
                binding.favouriteImageButton.setImageResource(R.drawable.ic_baseline_star_border_24)
            }
        }

        binding.lockImageButton.setOnClickListener {
            lock(it)
        }
               
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun initSymbols(note: Notes) {
        if(note.favourite) {
            favourite = true
            flagStar = true
            binding.favouriteImageButton.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            favourite = false
            flagStar = false
            binding.favouriteImageButton.setImageResource(R.drawable.ic_baseline_star_border_24)
        }

        if(note.lock!=null) {
            lock = note.lock
            lockFlag = true
            binding.lockImageButton.setImageResource(R.drawable.ic_baseline_lock_24)
        }
        else {
            lock = null
            lockFlag = false
            binding.lockImageButton.setImageResource(R.drawable.ic_baseline_lock_open_24)
        }
    }

    private fun lock(it : View) {
        var pass : String?
        val builder = AlertDialog.Builder(it.context)
        builder.setTitle("Password : ")

        val input = EditText(it.context)
        input.inputType =
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { dialog, which -> pass = input.text.toString()

            if(pass=="" || pass==null) {
                Toast.makeText(it.context,"Please enter password", Toast.LENGTH_SHORT).show()
            }
            else {
                Log.i("lock",pass.toString())
                setPass(pass)
            }

        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun setPass(pass : String?) {

        if(pass!=null &&  pass!="") {

            if (!lockFlag) {
                lock = pass
                lockFlag = true
                binding.lockImageButton.setImageResource(R.drawable.ic_baseline_lock_24)
            } else {
                lock = null
                lockFlag = false
                binding.lockImageButton.setImageResource(R.drawable.ic_baseline_lock_open_24)
            }
        }
    }

    private fun save() {

        if(binding.noteTextView.text.toString()!="" || binding.titleTextView.text.toString()!="") {
            val title = binding.titleTextView.text.toString().trim()
            val content = binding.noteTextView.text.toString().trim()
            val date = binding.dateTextView.text.toString()

            if(lock!=null)
                lock = noteViewModel.encrypt(lock.toString())

            currentNote = if(folderId!=-1)
                Notes(0, title, content, date, favourite, lock,folderId)
            else
                Notes(0, title, content, date, favourite, lock)

            goToCalledActivity(currentNote)

        }
        else {
            Toast.makeText(this@AddNote,"Please enter text",Toast.LENGTH_SHORT).show()
        }
    }

    private fun update() {
        if(binding.noteTextView.text.toString()!="" || binding.titleTextView.text.toString()!="") {

            currentNote.title = binding.titleTextView.text.toString().trim()
            currentNote.note = binding.noteTextView.text.toString().trim()
            currentNote.date = binding.dateTextView.text.toString()
            if(lock!=null)
                currentNote.lock = noteViewModel.encrypt(lock.toString())
            else
                currentNote.lock = lock
            currentNote.favourite = favourite

            noteViewModel.update(currentNote)
            adapter.notifyItemChanged(currentNote.note_id)
            goToCalledActivity()
        }
    }

    private fun back(isUpdate : Boolean) {

        val builder = AlertDialog.Builder(this)

        with(builder) {
            setTitle("Save? ")
            setMessage("Do you want Exit? ")

            //Save
            setPositiveButton("OK") { _: DialogInterface, _: Int ->
                if(isUpdate) {
                    update()
                    Log.i(OPERATION_COMPLETED_TAG,"Updated")
                }
                else {
                    save()
                    Log.i(OPERATION_COMPLETED_TAG,"Saved")
                }

            }

            //Cancel
            setNegativeButton("CANCEL") { _: DialogInterface, _: Int ->
                Log.i(OPERATION_COMPLETED_TAG,"Canceled")
                goToCalledActivity()
            }

        }
        val alertDialog = builder.create()
        alertDialog.show()

    }

    private fun goToCalledActivity(note: Notes) {
        val intent = Intent()
        intent.putExtra("note_object",note)
        setResult(RESULT_OK,intent)
        finish()
    }

    private fun goToCalledActivity() {
        val intent = Intent()
        setResult(RESULT_CANCELED,intent)
        finish()
    }
}