package com.example.ordonnance.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R

class Admin : AppCompatActivity() {

    lateinit var db: DatabaseHelper
    lateinit var list: ListView

    private val userIds = ArrayList<Int>()
    private val userDisplay = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        db = DatabaseHelper(this)
        list = findViewById(R.id.userList)

        loadUsers()
    }

    private fun loadUsers() {
        val cursor = db.getAllUsers()
        userIds.clear()
        userDisplay.clear()

        while (cursor.moveToNext()) {
            val id = cursor.getColumnIndex("id").let { if (it != -1) cursor.getInt(it) else -1 }
            val username = cursor.getColumnIndex("username").let { if (it != -1) cursor.getString(it) else "" }
            val firstName = cursor.getColumnIndex("firstName").let { if (it != -1) cursor.getString(it) else "" }
            val lastName = cursor.getColumnIndex("lastName").let { if (it != -1) cursor.getString(it) else "" }
            val email = cursor.getColumnIndex("email").let { if (it != -1) cursor.getString(it) else "" }
            val role = cursor.getColumnIndex("role").let { if (it != -1) cursor.getString(it) else "unknown" }

            userIds.add(id)
            userDisplay.add("$id | $username | $firstName $lastName | $email | $role")
        }


        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userDisplay)
        list.adapter = adapter

        list.setOnItemClickListener { _, _, position, _ ->
            val id = userIds[position]
            showRoleDialog(id)
        }
    }

    private fun showRoleDialog(userId: Int) {
        val roles = arrayOf("admin", "doctor", "client")

        AlertDialog.Builder(this)
            .setTitle("Change Role")
            .setItems(roles) { _, i ->
                db.updateUserRole(userId, roles[i])
                loadUsers()
            }
            .show()
    }
}
