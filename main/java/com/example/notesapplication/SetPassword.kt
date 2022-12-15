package com.example.notesapplication

interface SetPassword {
    object getFlagNumber {
        val value: Int
            get() = 3
    }

    fun encrypt(password: String) : String
    fun decrypt(password: String) : String

}