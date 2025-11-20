package com.example.project

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CompletionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completion)

        val btnFinish: Button = findViewById(R.id.btnFinish)
        btnFinish.setOnClickListener {
            finish() // go back to Home
        }
    }
}
