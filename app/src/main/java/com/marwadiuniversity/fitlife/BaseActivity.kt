package com.marwadiuniversity.fitlife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ⚠️ DO NOTHING with system bars here
    }

    protected fun setupToolbar(showBack: Boolean = false) {
        val toolbar = findViewById<Toolbar?>(R.id.toolbar) ?: return
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "FitLife"
            setDisplayHomeAsUpEnabled(showBack)
        }

        if (showBack) {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}
