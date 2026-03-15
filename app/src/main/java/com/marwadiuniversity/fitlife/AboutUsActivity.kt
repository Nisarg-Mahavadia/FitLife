package com.marwadiuniversity.fitlife

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.net.Uri
import android.widget.LinearLayout
class AboutUsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        findViewById<LinearLayout>(R.id.devLinkedin).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/in/nisarg-mahavadia-70b119293/"))
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.guideLinkedin).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/in/jigar-dave-577151121/"))
            startActivity(intent)
        }
        setupToolbar(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_about)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> true
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
