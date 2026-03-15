package com.marwadiuniversity.fitlife

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.marwadiuniversity.fitlife.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {

            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // ✅ User already logged in → go to Home
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // ❌ Not logged in → go to Login
                startActivity(Intent(this, LoginActivity::class.java))
            }

            finish()
        }
    }
}
