package com.example.ordonnance.register

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
import com.example.ordonnance.login.Login

class Register : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = DatabaseHelper(this)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val loginBtn=findViewById<Button>(R.id.loginBtn)

        registerBtn.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            if (db.register(user, pass)) {
                Toast.makeText(this, "Registered!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Login::class.java))
            } else {
                Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show()
            }
        }
        loginBtn.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
        }

    }
}
