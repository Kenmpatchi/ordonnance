package com.example.ordonnance.doctor

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R

class Doctor : AppCompatActivity() {

    lateinit var db: DatabaseHelper
    lateinit var spinner: Spinner

    var clientIds = ArrayList<Int>()   // Store IDs separately
    var clientNames = ArrayList<String>() // Names for spinner display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        db = DatabaseHelper(this)
        spinner = findViewById(R.id.clientSpinner)

        loadClientsIntoSpinner()

        val sickness = findViewById<EditText>(R.id.sickness)
        val text = findViewById<EditText>(R.id.prescriptionText)
        val sendBtn = findViewById<Button>(R.id.sendBtn)

        sendBtn.setOnClickListener {

            val selectedIndex = spinner.selectedItemPosition
            val clientId = clientIds[selectedIndex]

            val doctorId = 2     // 🔥 Replace this with logged-in doctor ID later

            val ok = db.insertPrescription(
                doctorId,
                clientId,
                sickness.text.toString(),
                text.text.toString()
            )

            if (ok)
                Toast.makeText(this, "Prescription Sent!", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Error sending prescription.", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadClientsIntoSpinner() {
        val cursor = db.getAllClients()

        clientIds.clear()
        clientNames.clear()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)

            clientIds.add(id)
            clientNames.add("$name")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            clientNames
        )

        spinner.adapter = adapter
    }
}

