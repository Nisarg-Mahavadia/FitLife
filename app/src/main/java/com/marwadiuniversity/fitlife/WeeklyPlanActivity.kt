package com.marwadiuniversity.fitlife

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WeeklyPlanActivity : BaseActivity() {

    private lateinit var tvMon: TextView
    private lateinit var tvTue: TextView
    private lateinit var tvWed: TextView
    private lateinit var tvThu: TextView
    private lateinit var tvFri: TextView
    private lateinit var tvSat: TextView
    private lateinit var tvSun: TextView

    private lateinit var cardMon: LinearLayout
    private lateinit var cardTue: LinearLayout
    private lateinit var cardWed: LinearLayout
    private lateinit var cardThu: LinearLayout
    private lateinit var cardFri: LinearLayout
    private lateinit var cardSat: LinearLayout
    private lateinit var cardSun: LinearLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var weeklyPlan: Map<String, String> = mapOf()

    private val defaultPlan = mapOf(
        "Monday" to "Full Body",
        "Tuesday" to "Arms",
        "Wednesday" to "Core",
        "Thursday" to "Legs",
        "Friday" to "Chest + Back",
        "Saturday" to "Shoulders",
        "Sunday" to "Rest"
    )

    private val activityMap = mapOf(
        "Full Body" to FullBodyActivity::class.java,
        "Arms" to ArmsActivity::class.java,
        "Core" to CoreActivity::class.java,
        "Legs" to LegsActivity::class.java,
        "Chest + Back" to ChestActivity::class.java,
        "Shoulders" to ShouldersActivity::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_plan)

        // 🔙 Enable ActionBar Back Arrow
        setupToolbar(true)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        getString(R.string.exercise_plan_title)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        loadWeeklyPlan()

        findViewById<Button>(R.id.btnCustomizeWeeklyPlan).setOnClickListener {
            startActivity(Intent(this, CustomizePlanActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            // 🔙 Handle back arrow click
            android.R.id.home -> {
                finish()
                true
            }

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

    private fun initViews() {
        tvMon = findViewById(R.id.tvMon)
        tvTue = findViewById(R.id.tvTue)
        tvWed = findViewById(R.id.tvWed)
        tvThu = findViewById(R.id.tvThu)
        tvFri = findViewById(R.id.tvFri)
        tvSat = findViewById(R.id.tvSat)
        tvSun = findViewById(R.id.tvSun)

        cardMon = findViewById(R.id.cardMon)
        cardTue = findViewById(R.id.cardTue)
        cardWed = findViewById(R.id.cardWed)
        cardThu = findViewById(R.id.cardThu)
        cardFri = findViewById(R.id.cardFri)
        cardSat = findViewById(R.id.cardSat)
        cardSun = findViewById(R.id.cardSun)
    }

    private fun loadWeeklyPlan() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->

                weeklyPlan = doc.get("weekly_plan") as? Map<String, String> ?: defaultPlan

                tvMon.text = getString(R.string.day_plan_format, "Monday", weeklyPlan["Monday"])
                tvTue.text = getString(R.string.day_plan_format, "Tuesday", weeklyPlan["Tuesday"])
                tvWed.text = getString(R.string.day_plan_format, "Wednesday", weeklyPlan["Wednesday"])
                tvThu.text = getString(R.string.day_plan_format, "Thursday", weeklyPlan["Thursday"])
                tvFri.text = getString(R.string.day_plan_format, "Friday", weeklyPlan["Friday"])
                tvSat.text = getString(R.string.day_plan_format, "Saturday", weeklyPlan["Saturday"])
                tvSun.text = getString(R.string.day_plan_format, "Sunday", weeklyPlan["Sunday"])

                setDynamicClickListeners()
            }
    }

    private fun setDynamicClickListeners() {
        cardMon.setOnClickListener { openActivityFor("Monday") }
        cardTue.setOnClickListener { openActivityFor("Tuesday") }
        cardWed.setOnClickListener { openActivityFor("Wednesday") }
        cardThu.setOnClickListener { openActivityFor("Thursday") }
        cardFri.setOnClickListener { openActivityFor("Friday") }
        cardSat.setOnClickListener { openActivityFor("Saturday") }
        cardSun.setOnClickListener { openActivityFor("Sunday") }
    }

    private fun openActivityFor(day: String) {
        val selected = weeklyPlan[day] ?: return

        if (selected == "Rest") {
            Toast.makeText(this, "$day is a rest day!", Toast.LENGTH_SHORT).show()
            return
        }

        val activityClass = activityMap[selected]

        if (activityClass != null) {
            startActivity(Intent(this, activityClass))
        } else {
            Toast.makeText(this, "No activity found for $selected", Toast.LENGTH_SHORT).show()
        }
    }
}
