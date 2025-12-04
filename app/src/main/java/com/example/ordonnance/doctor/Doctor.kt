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

    private val clientIds = ArrayList<Int>()
    private val clientNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        db = DatabaseHelper(this)
        spinner = findViewById(R.id.clientSpinner)

        loadClientsIntoSpinner()

        val sickness = findViewById<EditText>(R.id.sickness)
        val prescriptionText = findViewById<EditText>(R.id.prescriptionText)
        val sendBtn = findViewById<Button>(R.id.sendBtn)

        sendBtn.setOnClickListener {

            val selectedIndex = spinner.selectedItemPosition
            if (selectedIndex < 0 || selectedIndex >= clientIds.size) {
                Toast.makeText(this, "Please select a client", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val clientId = clientIds[selectedIndex]

            // Load doctor ID from SharedPreferences
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val doctorId = prefs.getInt("user_id", -1)

            if (doctorId == -1) {
                Toast.makeText(this, "Doctor ID not found!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ok = db.insertPrescription(
                doctorId,
                clientId,
                sickness.text.toString().trim(),
                prescriptionText.text.toString().trim()
            )

            if (ok)
                Toast.makeText(this, "Prescription Sent!", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Error sending prescription.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadClientsIntoSpinner() {
        val cursor = db.getAllClients()

        clientIds.clear()
        clientNames.clear()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)

            clientIds.add(id)
            clientNames.add(name)
        }

        cursor.close()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            clientNames
        )
        spinner.adapter = adapter
    }
}
