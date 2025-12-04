package com.example.ordonnance.patient

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R

class Patient : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        db = DatabaseHelper(this)

        // Get logged-in client ID from SharedPreferences
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        if (userId == -1) {
            // Safety check
            finish()
            return
        }

        val listView = findViewById<ListView>(R.id.prescriptionList)
        val cursor = db.getClientPrescriptions(userId)

        val items = ArrayList<String>()

        while (cursor.moveToNext()) {
            val sickness = cursor.getString(cursor.getColumnIndex("sickness"))
            val prescriptionText = cursor.getString(cursor.getColumnIndex("prescriptionText"))
            items.add("Sickness: $sickness\nPrescription: $prescriptionText")
        }

        cursor.close()

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
    }
}
