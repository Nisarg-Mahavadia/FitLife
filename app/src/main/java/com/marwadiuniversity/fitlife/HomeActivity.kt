package com.marwadiuniversity.fitlife

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp // ✅ Ensures firebase init if needed

class HomeActivity : BaseActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupToolbar(false) // Home page → no back button

        supportActionBar?.title = "FitLife"

        // ✅ Initialize Firebase again (safe even if already initialized)
        FirebaseApp.initializeApp(this)

        tabLayout = findViewById(R.id.tabLayoutHome)
        viewPager = findViewById(R.id.viewPagerHome)

        // Adapter for the two pages
        viewPager.adapter = HomePagerAdapter(this)

        // ✅ Use string resources for tab text (no hard coding)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.analysis_tab)  // "Your Analysis"
                else -> getString(R.string.tasks_tab)  // "Daily Tasks"
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
