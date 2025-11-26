package com.example.ordonnance.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ordonnance.DatabaseHelper
import com.example.ordonnance.R

class Admin : AppCompatActivity() {

    lateinit var db: DatabaseHelper
    lateinit var list: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        db = DatabaseHelper(this)
        list = findViewById(R.id.userList)

        loadUsers()
    }

    fun loadUsers() {
        val cursor = db.getAllUsers()
        val users = ArrayList<String>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val username = cursor.getString(1)
            val role = cursor.getString(3)
            users.add("$id | $username | $role")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
        list.adapter = adapter

        list.setOnItemClickListener { _, _, position, _ ->
            val userRow = users[position]
            val id = userRow.split(" | ")[0].toInt()

            showRoleDialog(id)
        }
    }

    fun showRoleDialog(userId: Int) {
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
