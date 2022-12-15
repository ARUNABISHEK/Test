package com.example.notesapplication.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notesapplication.variables.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Notes(

    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "note_id")
    var note_id : Int,

    @ColumnInfo(name = "title")
    var title : String?,

    @ColumnInfo(name = "note")
    var note : String?,

    @ColumnInfo(name = "date")
    var date : String?,

    @ColumnInfo(name = "favourite")
    var favourite : Boolean = false,

    @ColumnInfo(name = "lock")
    var lock : String? = null,

    @ColumnInfo(name = "folder_id")
    var folder_id : Int = -1,

    @ColumnInfo(name = "color_code")
    var color : String


) : java.io.Serializable
