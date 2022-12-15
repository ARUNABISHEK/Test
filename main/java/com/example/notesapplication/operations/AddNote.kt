package com.example.notesapplication.operations

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.notesapplication.R
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.databinding.AddNoteBinding
import com.example.notesapplication.fragments.FragmentPage.Companion.adapter
import com.example.notesapplication.fragments.FragmentPage.Companion.noteViewModel
import com.example.notesapplication.variables.COLOR
import com.example.notesapplication.variables.OPERATION_COMPLETED_TAG
import kotlinx.coroutines.*
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
    private var isUpdateColorFlag = false

    private var updateColor : Boolean = false

    override fun onBackPressed() {

        if(isUpdated &&
            binding.titleTextView.text.toString() == currentNote.title.toString() &&
            binding.noteTextView.text.toString() == currentNote.note.toString() &&
            currentNote.favourite == favourite &&
            currentNote.lock == lock)

            goToCalledActivity()

        else if(binding.titleTextView.text.toString() == "" &&
            binding.noteTextView.text.toString() == "" )
            goToCalledActivity()

        else {
            val builder = AlertDialog.Builder(this)

            with(builder) {

                if (isUpdated) {
                    setTitle("Update ")
                    setMessage("Do you want Exit? ")
                    setPositiveButton("Update") { _: DialogInterface, _: Int ->
                        update()
                        Log.i(OPERATION_COMPLETED_TAG, "Updated")

                    }

                } else {
                    setTitle("Save ")
                    setMessage("Do you want Exit? ")
                    setPositiveButton("Save") { _: DialogInterface, _: Int ->
                        save()
                        Log.i(OPERATION_COMPLETED_TAG, "Saved")
                    }

                }

                //Cancel
                setNegativeButton("Discard") { _: DialogInterface, _: Int ->
                    if (isUpdated) {
                        Toast.makeText(this@AddNote, "Canceled", Toast.LENGTH_SHORT).show()
                    }
                    Log.i(OPERATION_COMPLETED_TAG, "Canceled")
                    goToCalledActivity()
                }

            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
    @SuppressLint("ResourceAsColor", "ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.add_note)
        binding.dateTextView.text = Date().toString() //getCurrentDate()
        setStatusBar()
        //Intent Get Object
        if(intent.extras?.get("isUpdate") == true) {
            currentNote = intent.extras?.get("noteObject") as Notes
            isUpdated = intent.extras?.get("isUpdate") as Boolean
            setStatusBar()
            binding.titleTextView.text = currentNote.title?.toEditable()
            binding.noteTextView.text = currentNote.note?.toEditable()
            binding.noteFrame.setBackgroundColor(Color.parseColor(currentNote.color))

            if(currentNote.color == COLOR[3] ||currentNote.color == COLOR[2])
                setTextColor(Color.BLACK)

            else if(currentNote.color == COLOR[4] || currentNote.color == COLOR[5])
                setTextColor(Color.WHITE)

            initSymbols(currentNote)
        } else if(intent.extras?.get("folder_id")!=null) {
            folderId = intent.extras?.get("folder_id") as Int
            binding.noteFrame.setBackgroundColor(Color.parseColor(COLOR[noteViewModel.colorIndex]))
            setStatusBar()
            if(noteViewModel.colorIndex==3) {
                setTextColor(Color.BLACK)
            }
            else if(noteViewModel.colorIndex==4 || noteViewModel.colorIndex==5) {
                setTextColor(Color.WHITE)
            }
        } else {
            binding.noteFrame.setBackgroundColor(Color.parseColor(COLOR[noteViewModel.colorIndex]))
            setStatusBar()
            if(noteViewModel.colorIndex==3) {
                setTextColor(Color.BLACK)
            }
            else if(noteViewModel.colorIndex==4 || noteViewModel.colorIndex==5) {
                setTextColor(Color.WHITE)
            }
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

            else if(binding.titleTextView.text.toString() == "" &&
                        binding.noteTextView.text.toString() == "" )
                    goToCalledActivity()

            else
                back()
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

        binding.colorChange.setOnClickListener {

            updateColor = true
            val dialog = Dialog(this@AddNote)
            dialog.setContentView(R.layout.color_change);
            dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.window?.attributes?.windowAnimations = R.style.animation;
            setStatusBar()
            val color1 = dialog.findViewById<ImageView>(R.id.color1)
            val color2 = dialog.findViewById<ImageView>(R.id.color2)
            val color3 = dialog.findViewById<ImageView>(R.id.color3)
            val color4 = dialog.findViewById<ImageView>(R.id.color4)
            val color5 = dialog.findViewById<ImageView>(R.id.color5)
            val color6 = dialog.findViewById<ImageView>(R.id.color6)
            val done = dialog.findViewById<TextView>(R.id.done)

            done.setOnClickListener{
                dialog.dismiss()
            }

            color1.setOnClickListener {
                binding.noteFrame.setBackgroundColor(Color.parseColor(COLOR[0]))
                noteViewModel.colorIndex = 0
                setStatusBar()
            }

            color2.setOnClickListener {
                binding.noteFrame.setBackgroundColor(Color.parseColor(COLOR[1]))
                noteViewModel.colorIndex = 1
                setStatusBar()
            }

            color3.setOnClickListener {
                binding.noteFrame.setBackgroundColor(Color.parseColor(COLOR[2]))
                setTextColor(Color.BLACK)
                noteViewModel.colorIndex = 2
                setStatusBar()
            }

            color4.setOnClickListener {
                binding.noteFrame.setBackgroundColor(Color.parseColor(COLOR[3]))
                setTextColor(Color.BLACK)
                noteViewModel.colorIndex = 3
                setStatusBar()
            }

           color5.setOnClickListener {
               binding.noteFrame.setBackgroundColor(Color.parseColor(COLOR[4]))
               setTextColor(Color.WHITE)
               noteViewModel.colorIndex = 4
               setStatusBar()
            }

            color6.setOnClickListener {
                binding.noteFrame.setBackgroundColor(Color.parseColor(COLOR[5]))
                setTextColor(Color.WHITE)
                noteViewModel.colorIndex = 5
                setStatusBar()
            }

            dialog.show()
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
                Notes(0, title, content, date, favourite, lock,folderId,COLOR[noteViewModel.colorIndex])
            else
                Notes(0, title, content, date, favourite, lock,folderId,COLOR[noteViewModel.colorIndex])

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

            if(updateColor)
                currentNote.color = COLOR[noteViewModel.colorIndex]

            noteViewModel.update(currentNote)
            adapter.notifyItemChanged(currentNote.note_id)
            goToCalledActivity()
        }
    }

    private fun back() {

        val builder = AlertDialog.Builder(this)

        with(builder) {

            if(isUpdated) {
                setTitle("Update ")
                setMessage("Do you want Exit? ")
                setPositiveButton("Update") { _: DialogInterface, _: Int ->
                    update()
                    Log.i(OPERATION_COMPLETED_TAG,"Updated")

                }

            } else {
                setTitle("Save ")
                setMessage("Do you want Exit? ")
                setPositiveButton("Save") { _: DialogInterface, _: Int ->
                    save()
                    Log.i(OPERATION_COMPLETED_TAG,"Saved")
                }

            }

            //Cancel
            setNegativeButton("Discard") { _: DialogInterface, _: Int ->
                if(isUpdated) {
                    Toast.makeText(this@AddNote,"Canceled",Toast.LENGTH_SHORT).show()
                }
                Log.i(OPERATION_COMPLETED_TAG,"Canceled")
                goToCalledActivity()
            }

        }
        val alertDialog = builder.create()
        alertDialog.show()

    }

    private fun goToCalledActivity(note: Notes) {
        noteViewModel.colorIndex = (COLOR.indices).random()
        val intent = Intent()
        intent.putExtra("note_object",note)
        setResult(RESULT_OK,intent)
        finish()
    }

    private fun goToCalledActivity() {
        noteViewModel.colorIndex = (COLOR.indices).random()
        val intent = Intent()
        setResult(RESULT_CANCELED,intent)
        finish()
    }

    private fun setTextColor(colorCode: Int) {
        binding.noteTextView.setTextColor(colorCode)
        binding.titleTextView.setTextColor(colorCode)
        binding.dateTextView.setTextColor(colorCode)

        binding.titleTextView.setHintTextColor(colorCode)
        binding.noteTextView.setHintTextColor(colorCode)
    }

    private fun setStatusBar() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        if(isUpdated && !isUpdateColorFlag) {
            isUpdateColorFlag = true
            val color = Color.parseColor(currentNote.color.replace("#", "#66"))
            window.statusBarColor = color
        } else {
            val color = Color.parseColor(COLOR[noteViewModel.colorIndex].replace("#", "#66"))
            window.statusBarColor = color
        }
    }
}
