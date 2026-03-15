package com.marwadiuniversity.fitlife

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : BaseActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etGender: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var btnSave: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setupToolbar(true)

        supportActionBar?.title = "Edit Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etGender = findViewById(R.id.etGender)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        btnSave = findViewById(R.id.btnSaveProfile)

        loadExistingData()

        btnSave.setOnClickListener { saveProfile() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadExistingData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                etName.setText(doc.getString("name") ?: "")
                etGender.setText(doc.getString("gender") ?: "")
                etAge.setText(doc.get("age")?.toString() ?: "")
                etHeight.setText(doc.get("height")?.toString() ?: "")
                etWeight.setText(doc.get("weight")?.toString() ?: "")
            }
    }

    private fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return

        val name = etName.text.toString().trim()
        val age = etAge.text.toString().toIntOrNull()
        val gender = etGender.text.toString().trim()
        val height = etHeight.text.toString().toFloatOrNull()
        val weight = etWeight.text.toString().toFloatOrNull()

        if (name.isEmpty() || age == null || height == null || weight == null) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val bmi = weight / Math.pow((height / 100).toDouble(), 2.0)

        val map = mapOf(
            "name" to name,
            "age" to age,
            "gender" to gender,
            "height" to height,
            "weight" to weight,
            "bmi" to String.format("%.1f", bmi)
        )

        db.collection("users").document(uid)
            .update(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()

                // 🔥 Tell ProfileActivity to refresh
                setResult(RESULT_OK)

                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update Failed!", Toast.LENGTH_SHORT).show()
            }
    }
}
