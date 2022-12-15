package com.example.notesapplication.folderoption.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notesapplication.variables.FOLDER_TABLE_NAME

@Entity(tableName = FOLDER_TABLE_NAME)
data class Folder(

    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "folder_id")
    val folder_id : Int,

    @ColumnInfo(name = "folder_name")
    var folder_name : String = "Untitled",

    @ColumnInfo(name = "date")
    var date : String?,

    @ColumnInfo(name = "favourite")
    var favourite : Boolean = false,

    @ColumnInfo(name = "lock")
    var lock : String? = null

) : java.io.Serializable