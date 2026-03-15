package com.marwadiuniversity.fitlife

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : BaseActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAge: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvGoToLogin: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setupToolbar(false)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etAge = findViewById(R.id.etAge)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        spinnerGender = findViewById(R.id.spinnerGender)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val genders = listOf("Select","Male", "Female", "Other")
        spinnerGender.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)

        btnSignUp.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val age = etAge.text.toString().trim()
            val height = etHeight.text.toString().trim()
            val weight = etWeight.text.toString().trim()
            val gender = spinnerGender.selectedItem.toString()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (!validateSignup(name, email, age, height, weight, password, confirmPassword))
                return@setOnClickListener

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                        val userMap = hashMapOf(
                            "uid" to userId,
                            "name" to name,
                            "email" to email,
                            "age" to age.toInt(),
                            "gender" to gender,
                            "height" to height.toInt(),
                            "weight" to weight.toInt(),
                            "bmi" to "-",          // default BMI
                            "password" to password // ✅ STORED (as requested)
                        )

                        db.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user data!", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Signup failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateSignup(
        name: String,
        email: String,
        age: String,
        height: String,
        weight: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        if (name.isEmpty()) { etName.error = "Required"; return false }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid Email"; return false
        }
        if (age.isEmpty() || age.toIntOrNull() == null || age.toInt() < 10) {
            etAge.error = "Enter valid age"; return false
        }
        if (height.isEmpty() || height.toIntOrNull() == null || height.toInt() < 50) {
            etHeight.error = "Enter valid height"; return false
        }
        if (weight.isEmpty() || weight.toIntOrNull() == null || weight.toInt() < 20) {
            etWeight.error = "Enter valid weight"; return false
        }
        if (password.length < 6) {
            etPassword.error = "Min 6 characters"; return false
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords don't match"; return false
        }

        return true
    }
}
