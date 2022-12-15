package com.example.notesapplication.folderoption.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notesapplication.R
import com.example.notesapplication.database.NoteDatabase
import com.example.notesapplication.databinding.FragmentFolderBinding
import com.example.notesapplication.folderoption.adapter.FolderAdapter
import com.example.notesapplication.folderoption.db.repository.FolderRepository
import com.example.notesapplication.folderoption.viewModels.FolderViewModel
import com.example.notesapplication.folderoption.viewModels.FolderViewModelFactory

class FolderFragment(private val search : String = "") : Fragment() {


    private lateinit var binding : FragmentFolderBinding

    companion object{
        lateinit var folderViewModel: FolderViewModel
        lateinit var folderAdapter: FolderAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_folder, container,false)
        initViewModel(container)

        if(search=="")
            initRecyclerView(inflater)
        else
            displayFolderList(search)

        folderViewModel.message.observe(viewLifecycleOwner) { event_completion_obj ->
            event_completion_obj.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        return binding.root

    }

    private fun displayFolderList(text: String) {
        folderAdapter = FolderAdapter()
        binding.recyclerView.adapter = folderAdapter
        folderViewModel.allFolder.observe(viewLifecycleOwner) {
            folderAdapter.search(text)

        }
    }

    private fun initRecyclerView(inflater: LayoutInflater) {
        binding.recyclerView.layoutManager = GridLayoutManager(inflater.context,2)
        folderAdapter = FolderAdapter()

        binding.recyclerView.adapter = folderAdapter
        folderViewModel.allFolder.observe(viewLifecycleOwner) {
            folderAdapter.setFolder(it)
        }


    }

    private fun initViewModel(container: ViewGroup?) {
        val dao = NoteDatabase.getInstance(container?.context!!).FolderDao()
        val repository = FolderRepository(dao)
        val factory = FolderViewModelFactory(repository)

        //View_Model
        folderViewModel = ViewModelProvider(this@FolderFragment,factory)[FolderViewModel::class.java]
    }

}