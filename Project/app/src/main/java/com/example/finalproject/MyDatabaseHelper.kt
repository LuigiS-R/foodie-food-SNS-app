package com.example.finalproject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context : Context) : SQLiteOpenHelper(
    context, "myApp", null, 1){

    override fun onCreate(db: SQLiteDatabase){
        val createTableQuery1 = """
            CREATE TABLE users (
                username TEXT PRIMARY KEY,
                email TEXT,
                password TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery1);
    }

    fun insertUser(username: String, email: String, password: String){
        val db = writableDatabase
        val values = ContentValues().apply{
            put("username", username)
            put("email", email)
            put("password", password)
        }
        val result = db.insert("users", null, values)
    }

    fun getUsers(username: String): Cursor {
        val db = writableDatabase
        return db.rawQuery("SELECT * FROM users WHERE USERNAME = ?", arrayOf(username))
    }

    override fun onUpgrade(db: SQLiteDatabase?,
                           olderVersion: Int, newVersion: Int){

    }
}