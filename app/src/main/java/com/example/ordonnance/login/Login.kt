package com.example.ordonnance.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R
import com.example.ordonnance.admin.Admin
import com.example.ordonnance.doctor.Doctor
import com.example.ordonnance.patient.Patient
import com.example.ordonnance.register.Register

class Login : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val registerbtn=findViewById<Button>(R.id.registerRedirectBtn)

        loginBtn.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            val role = db.login(user, pass)

            when (role) {
                "admin" -> startActivity(Intent(this, Admin::class.java))
                "doctor" -> startActivity(Intent(this, Doctor::class.java))
                "client" -> startActivity(Intent(this, Patient::class.java))
                else -> Toast.makeText(this, "Invalid login!", Toast.LENGTH_SHORT).show()
            }
        }
        registerbtn.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }
}
