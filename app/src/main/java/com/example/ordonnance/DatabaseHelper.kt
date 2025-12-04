package com.example.ordonnance

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "app.db", null, 2) { // version 2 to handle new tables/columns

    override fun onCreate(db: SQLiteDatabase) {
        // Users table
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                firstName TEXT,
                lastName TEXT,
                phone TEXT,
                email TEXT UNIQUE,
                password TEXT,
                role TEXT
            )
        """)

        // Doctor details table
        db.execSQL("""
    CREATE TABLE doctor_details (
        doctorId INTEGER PRIMARY KEY,
        specialization TEXT,
        clinic TEXT,
        experience TEXT,
        license TEXT,
        validated INTEGER DEFAULT 1,
        FOREIGN KEY(doctorId) REFERENCES users(id)
    )
""")


        // Prescriptions table
        db.execSQL("""
            CREATE TABLE prescriptions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                doctorId INTEGER,
                clientId INTEGER,
                sickness TEXT,
                prescriptionText TEXT,
                FOREIGN KEY(doctorId) REFERENCES users(id),
                FOREIGN KEY(clientId) REFERENCES users(id)
            )
        """)

        // Default admin account
        db.execSQL("""
            INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'admin')
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For simplicity, drop all tables and recreate
        db.execSQL("DROP TABLE IF EXISTS doctor_details")
        db.execSQL("DROP TABLE IF EXISTS prescriptions")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // --- USERS METHODS ---

    fun register(
        username: String,
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        role: String = "client"
    ): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("username", username)
        cv.put("firstName", firstName)
        cv.put("lastName", lastName)
        cv.put("phone", phone)
        cv.put("email", email)
        cv.put("password", password)
        cv.put("role", role)
        return db.insert("users", null, cv) > 0
    }

    fun login(userInput: String, password: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT role FROM users WHERE (username=? OR email=?) AND password=?",
            arrayOf(userInput, userInput, password)
        )
        val role = cursor.getColumnIndex("role").let { idx ->
            if (cursor.moveToFirst() && idx != -1) cursor.getString(idx) else null
        }
        cursor.close()
        return role
    }

    fun getUserId(userInput: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM users WHERE username=? OR email=?",
            arrayOf(userInput, userInput)
        )
        val id = cursor.getColumnIndex("id").let { idx ->
            if (cursor.moveToFirst() && idx != -1) cursor.getInt(idx) else -1
        }

        cursor.close()
        return id
    }

    fun getAllUsers(): Cursor {
        return readableDatabase.rawQuery("SELECT * FROM users", null)
    }

    fun updateUserRole(userId: Int, newRole: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("role", newRole)
        val updated = db.update("users", cv, "id=?", arrayOf(userId.toString())) > 0

        // 🔹 If role changed to doctor, create doctor profile if not exists
        if (updated && newRole == "doctor") {
            val cursor = db.rawQuery(
                "SELECT doctorId FROM doctor_details WHERE doctorId=?",
                arrayOf(userId.toString())
            )
            if (!cursor.moveToFirst()) {
                val cvDoctor = ContentValues()
                cvDoctor.put("doctorId", userId)
                cvDoctor.put("specialization", "")
                cvDoctor.put("clinic", "")
                cvDoctor.put("experience", "")
                cvDoctor.put("license", "")
                db.insert("doctor_details", null, cvDoctor)
            }
            cursor.close()
        }

        return updated
    }


    // --- DOCTOR DETAILS ---

    fun getDoctorProfileValidated(doctorId: Int): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT validated FROM doctor_details WHERE doctorId=?",
            arrayOf(doctorId.toString())
        )
        val validated = if (cursor.moveToFirst()) cursor.getInt(0) == 1 else false
        cursor.close()
        return validated
    }


    // --- PRESCRIPTIONS ---

    fun insertPrescription(doctorId: Int, clientId: Int, sickness: String, prescriptionText: String): Boolean {
        val cv = ContentValues()
        cv.put("doctorId", doctorId)
        cv.put("clientId", clientId)
        cv.put("sickness", sickness)
        cv.put("prescriptionText", prescriptionText)
        return writableDatabase.insert("prescriptions", null, cv) > 0
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
    fun updateDoctorProfile(
        doctorId: Int,
        specialization: String,
        clinic: String,
        experience: String,
        license: String
    ): Boolean {
        val cv = ContentValues()
        cv.put("specialization", specialization)
        cv.put("clinic", clinic)
        cv.put("experience", experience)
        cv.put("license", license)
        cv.put("validated", 1)  // mark profile as completed
        return writableDatabase.update(
            "doctor_details",
            cv,
            "doctorId=?",
            arrayOf(doctorId.toString())
        ) > 0
    }



}
