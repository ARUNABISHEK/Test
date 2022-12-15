package com.example.notesapplication.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplication.R
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.databinding.NotesListBinding
import com.example.notesapplication.fragments.FragmentPage.Companion.adapter
import com.example.notesapplication.fragments.FragmentPage.Companion.noteViewModel
import com.example.notesapplication.operations.AddNote
import com.example.notesapplication.view_models.NoteViewModel

class Adapter(private val isStared : Boolean = false) : RecyclerView.Adapter<ViewHolder>() {

    private var noteList = mutableListOf<Notes>()
    private var myList = mutableListOf<Notes>()
    private var folderId : Int = -1
    var insideFolderIsNoteAvailable = false
    private lateinit var binding : NotesListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.notes_list,parent,false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(folderId!=-1) {
            if(myList[position].folder_id == folderId) {
                holder.bind(myList[position])
            }
        }
        if(isStared) {
            holder.bindStared(myList[position])
        }
        else {
            holder.bind(myList[position])
        }

        holder.binding.cardView.setCardBackgroundColor(Color.parseColor(myList[position].color))

    }

    fun search(search : String,id : Int=folderId) {
        myList.clear()
        if(id!=folderId) {
            for(item in noteList) {
                if( item.folder_id == id && ( item.title?.lowercase()?.contains(search.lowercase())==true ||
                    item.note?.lowercase()?.contains(search.lowercase())==true)) {

                    myList.add(item)
                }
            }
        }
        else {
            for (item in noteList) {
                if (item.title?.lowercase()?.contains(search.lowercase()) == true ||
                    item.note?.lowercase()?.contains(search.lowercase()) == true
                ) {
                    Log.i("log", "Log")
                    myList.add(item)
                }
            }
        }

        notifyDataSetChanged()
    }

    fun getNote(index : Int) : Notes {
        return myList[index]
    }

    fun setNote(list : List<Notes>) {
        noteList.clear()
        noteList.addAll(list)
        myList.clear()
        myList.addAll(noteList)

        notifyDataSetChanged()
    }

    fun fileInFolder(id : Int) {

        myList.clear()

        for(i in noteList) {
            if(i.folder_id == id) {
                myList.add(i)
            }
        }

        notifyDataSetChanged()

        insideFolderIsNoteAvailable = myList.isNotEmpty()
    }

}

class ViewHolder(val binding: NotesListBinding) : RecyclerView.ViewHolder(binding.root) {

    private fun setTextColor(colorCodeTitle : Int,colorCode : Int) {
        binding.title.setTextColor(colorCodeTitle)
        binding.note.setTextColor(colorCode)
        binding.date.setTextColor(colorCode)

        binding.title.setHintTextColor(colorCode)
        binding.note.setHintTextColor(colorCode)
    }

    fun bind(note : Notes) {

        binding.title.text = note.title
        binding.note.text = note.note
        binding.date.text = note.date
        binding.indexLock.setImageResource(R.drawable.ic_baseline_lock_open_24)
        binding.indexPin.setImageResource(R.drawable.ic_baseline_star_border_24)

        setTextColor(Color.BLACK,R.color.black)


        if(note.lock!=null) {
            //binding.cardView.setCardBackgroundColor(Color.RED)
            binding.note.text = "Password Producted"
            binding.indexLock.setImageResource(R.drawable.ic_baseline_lock_24)
        }

        if(note.favourite) {
            binding.indexPin.setImageResource(R.drawable.ic_baseline_star_24)
        }
        //Update
        binding.listItem.setOnClickListener {
            openNote(it.context,note)
        }

        binding.indexPin.setOnClickListener {
            if(note.favourite) {
                binding.indexPin.setImageResource(R.drawable.ic_baseline_star_border_24)
                note.favourite = false
                noteViewModel.update(note)
                adapter.notifyItemChanged(note.note_id)
            }
            else {
                binding.indexPin.setImageResource(R.drawable.ic_baseline_star_24)
                note.favourite = true
                noteViewModel.update(note)
                adapter.notifyItemChanged(note.note_id)
            }
        }

        binding.indexLock.setOnClickListener {
            lockDialogBox(it,note,noteViewModel)
        }

        binding.listItem.setOnLongClickListener{

            //popup(it,note)
            true
        }

    }

//    private fun popup(v: View,note: Notes) {
//        val popupMenu = PopupMenu(v.context, v)
//        popupMenu.menu.add("Copy")
//        popupMenu.menu.add("Move")
//        popupMenu.menu.add("Rename")
//        popupMenu.setOnMenuItemClickListener { item ->
//
//            if (item.title == "Copy") {
//                copyNote = note
//                Toast.makeText(v.context,"Note copied",Toast.LENGTH_SHORT).show()
//            }
//            if (item.title == "Move") {
//
//            }
//            if (item.title == "Rename") {
//
//            }
//            true
//        }
//        popupMenu.show()
//    }

    fun bindStared(note: Notes) {
        binding.title.text = note.title
        binding.note.text = note.note
        binding.date.text = note.date
        binding.indexLock.setImageResource(R.drawable.ic_baseline_lock_open_24)
        binding.indexPin.setImageResource(R.drawable.ic_baseline_star_24)

        setTextColor(Color.BLACK,R.color.black)

        if(note.lock!=null) {
            //binding.cardView.setCardBackgroundColor(Color.RED)
            binding.note.text = "Password Producted"
            binding.indexLock.setImageResource(R.drawable.ic_baseline_lock_24)
        }

        //Update
        binding.listItem.setOnClickListener {
            openNote(it.context,note)
        }

        binding.indexPin.setOnClickListener {
            if(note.favourite) {
                note.favourite = false
                noteViewModel.update(note)
            }
        }

        binding.indexLock.setOnClickListener {
            lockDialogBox(it,note,noteViewModel)
        }
    }


    private fun lockDialogBox(it: View, note: Notes, noteViewModel: NoteViewModel) {
        var pass : String?
        if(note.lock==null) {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Set Password : ")

            val input = EditText(it.context)
            input.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)

            builder.setPositiveButton(
                "OK"
            ) { dialog, which ->
                pass = input.text.toString()

                if (pass == "" || pass == null) {
                    Toast.makeText(it.context, "Please enter password", Toast.LENGTH_SHORT)
                        .show()
                    lockDialogBox(it, note, noteViewModel)

                } else {
                    Log.i("lock", pass.toString())
                    note.lock = noteViewModel.encrypt(pass.toString())
                    noteViewModel.update(note)
                    adapter.notifyItemChanged(note.note_id)
                }

            }

            builder.setNegativeButton(
                "Cancel"
            ) { dialog, which ->
                Toast.makeText(it.context, "Canceled", Toast.LENGTH_SHORT)
                    .show()
                dialog.cancel()
            }

            builder.show()
        }

        else {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Password : ")

            val input = EditText(it.context)
            input.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)

            builder.setPositiveButton(
                "OK"
            ) { _, _ ->
                pass = input.text.toString()
                val decrypt = noteViewModel.decrypt(note.lock.toString())
                if (pass == decrypt) {
                    note.lock = null
                    noteViewModel.update(note)
                    adapter.notifyItemChanged(note.note_id)
                }

            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.cancel()
                Toast.makeText(it.context, "Please enter password", Toast.LENGTH_SHORT)
                    .show()}

            builder.show()
        }
    }

    private fun openNote(it: Context, note: Notes) {
        var text = ""
        if(note.lock!=null) {
            val builder = AlertDialog.Builder(it)
            builder.setCancelable(false)
            builder.setTitle("Password : ")

            val input = EditText(it)
            input.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)

            builder.setPositiveButton(
                "OK"
            ) { dialog, which ->

                text = input.text.toString()

                val decrypt : String = noteViewModel.decrypt(note.lock.toString())

                if(text=="") {
                    Toast.makeText(it, "Please enter password", Toast.LENGTH_SHORT)
                        .show()
                    openNote(it, note)

                }
                else {
                    if (text == decrypt) {
                        val i = Intent(it, AddNote::class.java)
                        i.putExtra("noteObject", note)
                        i.putExtra("isUpdate", true)
                        startActivity(it, i, null)
                    } else {
                        Toast.makeText(it, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, which ->
                Toast.makeText(it, "Canceled", Toast.LENGTH_SHORT)
                    .show()
                dialog.cancel() }

            builder.show()
        } else {
            val i = Intent(it, AddNote::class.java)
            i.putExtra("noteObject",note)
            i.putExtra("isUpdate",true)
            startActivity(it, i, null)
        }
    }

}

