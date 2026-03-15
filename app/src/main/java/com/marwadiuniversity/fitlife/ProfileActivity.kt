package com.marwadiuniversity.fitlife

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : BaseActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvBmi: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnDeleteProfile: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupToolbar(true)

        supportActionBar?.title = "Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvGender = findViewById(R.id.tvGender)
        tvAge = findViewById(R.id.tvAge)
        tvBmi = findViewById(R.id.tvBmi)
        btnLogout = findViewById(R.id.btnLogout)
        btnDeleteProfile = findViewById(R.id.btnDeleteProfile)

        loadProfile()

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Setup Activity Result Launcher for editing profile
        val editProfileLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                loadProfile()
                setResult(RESULT_OK)
            }
        }

        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            editProfileLauncher.launch(Intent(this, EditProfileActivity::class.java))
        }

        // ✅ DELETE PROFILE LOGIC
        btnDeleteProfile.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setTitle("Delete Profile")
            builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            builder.setPositiveButton("YES") { _, _ ->
                deleteUserAccount()
            }
            builder.setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    private fun deleteUserAccount() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        // 1️⃣ Delete Firestore document
        db.collection("users").document(uid)
            .delete()
            .addOnSuccessListener {

                // 2️⃣ Delete Authentication account
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            // 3️⃣ Sign out just to be safe
                            auth.signOut()

                            // 4️⃣ Redirect to LoginActivity and clear back stack
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                            finish()

                        } else {
                            Toast.makeText(
                                this,
                                "Re-authentication required. Please login again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete account.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_OK)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                tvName.text = "Name: ${doc.getString("name") ?: "-"}"
                tvEmail.text = doc.getString("email") ?: "-"
                tvGender.text = doc.getString("gender") ?: "-"
                tvAge.text = doc.get("age")?.toString() ?: "-"
                tvBmi.text = doc.get("bmi")?.toString() ?: "-"
            }
    }
}