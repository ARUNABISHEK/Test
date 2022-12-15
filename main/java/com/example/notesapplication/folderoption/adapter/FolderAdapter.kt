package com.example.notesapplication.folderoption.adapter


import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplication.R
import com.example.notesapplication.databinding.FolderListBinding
import com.example.notesapplication.folderoption.InsideFolderActivity
import com.example.notesapplication.folderoption.db.model.Folder
import com.example.notesapplication.folderoption.fragment.FolderFragment.Companion.folderAdapter
import com.example.notesapplication.folderoption.fragment.FolderFragment.Companion.folderViewModel
import com.example.notesapplication.fragments.FragmentPage.Companion.adapter
import com.example.notesapplication.fragments.FragmentPage.Companion.noteViewModel

class FolderAdapter : RecyclerView.Adapter<FolderViewHolder>() {

    private var folderList = mutableListOf<Folder>()
    private var myList = mutableListOf<Folder>()

    private lateinit var binding : FolderListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.folder_list,parent,false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(myList[position])

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context,InsideFolderActivity::class.java)
            intent.putExtra("folder_id",myList[position].folder_id)
            startActivity(it.context,intent,null)
        }

        holder.itemView.setOnLongClickListener {v ->

            val currentFolder = myList[position]

            val popupMenu = PopupMenu(v.context, v)
            popupMenu.menu.add("Rename")
            popupMenu.menu.add("Delete")

            popupMenu.setOnMenuItemClickListener { item ->

                if (item.title == "Rename") {
                    val builder = AlertDialog.Builder(v.context)
                    builder.setCancelable(false)
                    builder.setTitle("New Name : ")

                    val input = EditText(v.context)
                    input.text = currentFolder.folder_name.toEditable()
                    input.inputType =
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                    builder.setView(input)

                    builder.setPositiveButton(
                        "Rename"
                    ) { _, _ ->

                        currentFolder.folder_name = input.text.toString()
                        folderViewModel.update(currentFolder)
                        folderAdapter.notifyItemChanged(position)
//                        Toast.makeText(v.context,"Renamed...",Toast.LENGTH_SHORT).show()
                    }
                    builder?.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ ->
                        dialog.cancel()
                        Toast.makeText(v.context, "Canceled", Toast.LENGTH_SHORT)
                            .show()
                        folderAdapter.notifyDataSetChanged() }

                    builder?.show()
                }

                if (item.title == "Delete") {
                    val builder = AlertDialog.Builder(v.context)
                    builder.setCancelable(false)
                    builder.setTitle("Delete? : ")

                    builder.setMessage("Do you want delete ${currentFolder.folder_name} folder")
                    builder.setPositiveButton(
                        "Delete"
                    ) { _, _ ->

                        noteViewModel.deleteInFolder(currentFolder.folder_id)
                        adapter.notifyDataSetChanged()

                        folderViewModel.delete(currentFolder)
                        folderAdapter.notifyItemRemoved(currentFolder.folder_id)
//                        Toast.makeText(v.context,"Deleted...",Toast.LENGTH_SHORT).show()
                    }
                    builder?.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ ->
                        dialog.cancel()
                        Toast.makeText(v.context, "Canceled", Toast.LENGTH_SHORT)
                            .show()
                        folderAdapter.notifyDataSetChanged() }

                    builder?.show()
                }

                true
            }
            popupMenu.show()

            true
        }

    }

    override fun getItemCount(): Int {
        return myList.size
    }

    fun setFolder(list : List<Folder>) {
        folderList.clear()
        folderList.addAll(list)
        myList.clear()
        myList.addAll(folderList)

        notifyDataSetChanged()
    }

    fun getFolder(id : Int) : Folder {
        return myList[id]
    }
    fun search(search : String) {
        myList.clear()
        for(item in folderList) {
            if(item.folder_name?.lowercase()?.contains(search.lowercase())==true) {
                myList.add(item)
            }
        }
        notifyDataSetChanged()
    }

    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}

class FolderViewHolder(val binding: FolderListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(folder: Folder) {
        binding.imageView.setImageResource(R.drawable.folder)
        binding.textView.text = folder.folder_name
    }

}