package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvBmi: TextView
    private lateinit var btnLogout: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvGender = findViewById(R.id.tvGender)
        tvAge = findViewById(R.id.tvAge)
        tvBmi = findViewById(R.id.tvBmi)
        btnLogout = findViewById(R.id.btnLogout)

        loadProfile()

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            startActivityForResult(Intent(this, EditProfileActivity::class.java), 3001)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_OK)  // Tell HomeActivity to refresh
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 3001 && resultCode == RESULT_OK) {
            loadProfile()      // 🔥 Refresh Profile instantly
            setResult(RESULT_OK)  // Notify HomeActivity also
        }
    }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                tvName.text = "Name: ${doc.getString("name") ?: "-"}"
                tvEmail.text = "Email: ${doc.getString("email") ?: "-"}"
                tvGender.text = "Gender: ${doc.getString("gender") ?: "-"}"
                tvAge.text = "Age: ${doc.get("age") ?: "-"}"
                tvBmi.text = "BMI: ${doc.get("bmi") ?: "-"}"
            }
    }
}
