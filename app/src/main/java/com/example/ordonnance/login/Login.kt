package com.example.ordonnance.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R
import com.example.ordonnance.admin.Admin
import com.example.ordonnance.doctor.Doctor
import com.example.ordonnance.doctor.DoctorDetailsActivity
import com.example.ordonnance.patient.Patient
import com.example.ordonnance.register.Register

class Login : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val identifier = findViewById<EditText>(R.id.usernameOrEmail)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val registerBtn = findViewById<Button>(R.id.registerRedirectBtn)

        loginBtn.setOnClickListener {
            val idText = identifier.text.toString().trim()
            val pass = password.text.toString().trim()

            if (idText.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter username/email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val role = db.login(idText, pass)

            if (role != null) {

                // Get user ID
                val userId = db.getUserId(idText)

                // Save to SharedPreferences
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                prefs.edit()
                    .putInt("user_id", userId)
                    .putString("role", role)
                    .apply()

                // Redirect based on role
                when (role) {
                    "admin" -> startActivity(Intent(this, Admin::class.java))

                    "doctor" -> {
                        // 🔹 Check if doctor profile is completed
                        val validated = db.getDoctorProfileValidated(userId)
                        if (!validated) {
                            // Redirect to complete doctor profile
                            startActivity(Intent(this, DoctorDetailsActivity::class.java))
                        } else {
                            startActivity(Intent(this, Doctor::class.java))
                        }
                    }

                    "client" -> startActivity(Intent(this, Patient::class.java))
                }

                finish()
            } else {
                Toast.makeText(this, "Invalid username/email or password!", Toast.LENGTH_SHORT).show()
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }
}
