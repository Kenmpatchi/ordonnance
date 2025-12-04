package com.example.ordonnance.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R
import com.example.ordonnance.login.Login

class Register : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = DatabaseHelper(this)

        val username = findViewById<EditText>(R.id.username)
        val firstName = findViewById<EditText>(R.id.firstName)
        val lastName = findViewById<EditText>(R.id.lastName)
        val phone = findViewById<EditText>(R.id.phone)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        registerBtn.setOnClickListener {
            val user = username.text.toString().trim()
            val fName = firstName.text.toString().trim()
            val lName = lastName.text.toString().trim()
            val phoneNumber = phone.text.toString().trim()
            val emailAddr = email.text.toString().trim()
            val pass = password.text.toString().trim()

            if (user.isEmpty() || fName.isEmpty() || lName.isEmpty() || phoneNumber.isEmpty() || emailAddr.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = db.register(user, fName, lName, phoneNumber, emailAddr, pass)
            if (success) {
                Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Login::class.java))
                finish()
            } else {
                Toast.makeText(this, "User with this username or email already exists!", Toast.LENGTH_SHORT).show()
            }
        }

        loginBtn.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}
