package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToSignUp: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 AUTO LOGIN CHECK (important part)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (!validateLogin(email, password)) return@setOnClickListener

            // ✅ Firebase login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email format"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return false
        }
        return true
    }
}
