package com.marwadiuniversity.fitlife

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make sure you create this XML file in layout folder: activity_splash.xml
        setContentView(R.layout.activity_splash)

        // ✅ After splash screen delay → Open LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close splash so user can't go back to it
        }, 2000) // 2 seconds delay
    }
}
