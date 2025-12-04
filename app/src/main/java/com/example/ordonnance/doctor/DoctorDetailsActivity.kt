package com.example.ordonnance.doctor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R

class DoctorDetailsActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_details)

        db = DatabaseHelper(this)

        val speciality = findViewById<EditText>(R.id.speciality)
        val clinicAddress = findViewById<EditText>(R.id.clinicAddress)
        val experience = findViewById<EditText>(R.id.experience)
        val license = findViewById<EditText>(R.id.license)
        val saveBtn = findViewById<Button>(R.id.saveBtn)

        // Get doctor ID from SharedPreferences
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val doctorId = prefs.getInt("user_id", -1)

        if (doctorId == -1) {
            Toast.makeText(this, "Error: Doctor ID not found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        saveBtn.setOnClickListener {
            val spec = speciality.text.toString().trim()
            val clinic = clinicAddress.text.toString().trim()
            val exp = experience.text.toString().trim()
            val lic = license.text.toString().trim()

            if (spec.isEmpty() || clinic.isEmpty() || exp.isEmpty() || lic.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = db.updateDoctorProfile(doctorId, spec, clinic, exp, lic)

            if (success) {
                Toast.makeText(this, "Profile completed!", Toast.LENGTH_SHORT).show()
                // Go to main Doctor activity
                startActivity(Intent(this, Doctor::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
