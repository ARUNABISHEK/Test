package com.example.notesapplication.folderoption.adapter


import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplication.R
import com.example.notesapplication.databinding.FolderListBinding
import com.example.notesapplication.folderoption.InsideFolderActivity
import com.example.notesapplication.folderoption.db.model.Folder

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

    fun search(search : String) {
        myList.clear()
        for(item in folderList) {
            if(item.folder_name?.lowercase()?.contains(search.lowercase())==true) {
                myList.add(item)
            }
        }
        notifyDataSetChanged()
    }

}

class FolderViewHolder(val binding: FolderListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(folder: Folder) {
        binding.imageView.setImageResource(R.drawable.folder)
        binding.textView.text = folder.folder_name
    }

}