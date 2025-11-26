package com.example.ordonnance.patient

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R

class Patient : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        db = DatabaseHelper(this)

        val list = findViewById<ListView>(R.id.prescriptionList)
        val cursor = db.getClientPrescriptions(3) // replace with logged-in client id

        val items = ArrayList<String>()

        while (cursor.moveToNext()) {
            val sick = cursor.getString(cursor.getColumnIndex("sickness"))
            val txt = cursor.getString(cursor.getColumnIndex("prescriptionText"))
            items.add("Sickness: $sick\nPrescription: $txt")
        }

        list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
    }
}
