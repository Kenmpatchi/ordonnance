package com.example.ordonnance

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "app.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT,
                role TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE prescriptions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                doctorId INTEGER,
                clientId INTEGER,
                sickness TEXT,
                prescriptionText TEXT
            )
        """)

        // Create default admin account
        db.execSQL("INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'admin')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS prescriptions")
        onCreate(db)
    }

    fun register(username: String, password: String, role: String = "client"): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("username", username)
        cv.put("password", password)
        cv.put("role", role)
        return db.insert("users", null, cv) > 0
    }

    fun login(username: String, password: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT role FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )
        return if (cursor.moveToFirst()) cursor.getString(0) else null
    }

    fun getAllUsers(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM users", null)
    }

    fun updateUserRole(userId: Int, newRole: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("role", newRole)
        return db.update("users", cv, "id=?", arrayOf(userId.toString())) > 0
    }

    fun insertPrescription(doctorId: Int, clientId: Int, sickness: String, text: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("doctorId", doctorId)
        cv.put("clientId", clientId)
        cv.put("sickness", sickness)
        cv.put("prescriptionText", text)
        return db.insert("prescriptions", null, cv) > 0
    }

    fun getClientPrescriptions(clientId: Int): Cursor {
        return readableDatabase.rawQuery(
            "SELECT * FROM prescriptions WHERE clientId=?",
            arrayOf(clientId.toString())
        )
    }
    fun getAllClients(): Cursor {
        return readableDatabase.rawQuery(
            "SELECT id, username FROM users WHERE role='client'",
            null
        )
    }

}
