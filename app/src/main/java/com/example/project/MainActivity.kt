package com.example.project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // 🔥 Check if user is already logged in
        if (auth.currentUser != null) {
            // User is logged in → go directly to Home
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            // User NOT logged in → go to Login page
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
