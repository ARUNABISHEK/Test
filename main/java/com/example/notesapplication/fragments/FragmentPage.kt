package com.example.notesapplication.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplication.R
import com.example.notesapplication.adapter.Adapter
import com.example.notesapplication.database.NoteDatabase
import com.example.notesapplication.database.model.Notes
import com.example.notesapplication.database.repository.NoteRepository
import com.example.notesapplication.databinding.FragmentPageBinding
import com.example.notesapplication.view_models.NoteViewModel
import com.example.notesapplication.view_models.NoteViewModelFactory

class FragmentPage(private val isStared : Boolean = false,
                   private val search : String = "") : Fragment() {

    private lateinit var binding : FragmentPageBinding

    companion object{
        lateinit var noteViewModel: NoteViewModel
        lateinit var adapter: Adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_page, container,false)
        initViewModel(container)

        if(search=="")
            initRecyclerView(inflater)
        else
            displayNoteList(search)

        noteViewModel.message.observe(viewLifecycleOwner) { event_completion_obj ->
            event_completion_obj.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun displayNoteList(text: String) {
        adapter = Adapter()
        binding.recyclerView.adapter = adapter
        noteViewModel.allNotes.observe(viewLifecycleOwner) {
            //adapter.setNote(it)
            adapter.search(text)

        }
//
    }

    fun initRecyclerView(inflater: LayoutInflater) {

        //Initialize_Recycler_View
        binding.recyclerView.layoutManager = LinearLayoutManager(inflater.context)

        if(isStared) {
            adapter = Adapter(true)
            binding.recyclerView.adapter = adapter
            noteViewModel.favouriteNote.observe(viewLifecycleOwner) {
                adapter.setNote(it)
            }
        }
        else {

            adapter = Adapter()
            binding.recyclerView.adapter = adapter
            noteViewModel.allNotes.observe(viewLifecycleOwner, Observer{
                adapter.setNote(it)
            })
        }

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
                if(currentNote.lock==null)
                    deleteDialogBox(currentNote)
                else
                    PasswordDialogBox(currentNote)

            }
        }).attachToRecyclerView(binding.recyclerView)

    }

    private fun initViewModel(container : ViewGroup?) {
        val dao = NoteDatabase.getInstance(container?.context!!).noteDao()
        val repository = NoteRepository(dao)
        val factory = NoteViewModelFactory(repository)

        //View_Model
        noteViewModel = ViewModelProvider(this@FragmentPage,factory)[NoteViewModel::class.java]
    }

    private fun deleteDialogBox(note : Notes) {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Delete :")
        builder?.setMessage("Do you want to delete? ")

        builder?.setPositiveButton(
            "OK"
        ) { dialog, which ->
            noteViewModel.delete(note)
            adapter.notifyItemRemoved(note.note_id)
        }
        builder?.setNegativeButton(
            "Cancel"
        ) { dialog, which ->

            adapter.notifyDataSetChanged()
            dialog.cancel()

        }

        builder?.show()
    }

    private fun PasswordDialogBox(note: Notes) {
        var pass = ""
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Password : ")

        val input = EditText(context)
        input.inputType =
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder?.setView(input)

        builder?.setPositiveButton(
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
        builder?.setNegativeButton(
            "Cancel"
        ) { dialog, _ ->
            dialog.cancel()
            adapter.notifyDataSetChanged() }

        builder?.show()
    }

}
