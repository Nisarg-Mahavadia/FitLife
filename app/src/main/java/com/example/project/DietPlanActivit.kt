package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DietPlanActivity : AppCompatActivity() {

    private lateinit var tvGoal: TextView
    private lateinit var tvBreakfastItems: TextView
    private lateinit var tvLunchItems: TextView
    private lateinit var tvSnacksItems: TextView
    private lateinit var tvDinnerItems: TextView
    private lateinit var tvWaterValue: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_plan)

        // 🔙 Enable back arrow in ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = "Diet Plan"

        tvGoal = findViewById(R.id.tvGoal)
        tvBreakfastItems = findViewById(R.id.tvBreakfastItems)
        tvLunchItems = findViewById(R.id.tvLunchItems)
        tvSnacksItems = findViewById(R.id.tvSnacksItems)
        tvDinnerItems = findViewById(R.id.tvDinnerItems)
        tvWaterValue = findViewById(R.id.tvWaterValue)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadDietPlan()
    }

    // 🔥 Menu (Profile Icon)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            // 🔙 Handle Back Arrow Click
            android.R.id.home -> {
                finish()   // or onBackPressed()
                true
            }

            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadDietPlan() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val goal = doc.getString("goal_type") ?: "maintain_weight"
                tvGoal.text = "Goal: ${goal.replace("_", " ").uppercase()}"

                when (goal) {
                    "weight_gain" -> loadWeightGainDiet()
                    "weight_loss" -> loadWeightLossDiet()
                    else -> loadMaintainDiet()
                }
            }
    }

    private fun loadWeightGainDiet() {
        tvBreakfastItems.text =
            "- Oats with milk & nuts\n- Banana smoothie\n- Peanut butter toast"

        tvLunchItems.text =
            "- Roti + Paneer sabzi\n- Dal + Rice\n- Salad + Curd"

        tvSnacksItems.text =
            "- Dry fruits mix\n- Fruit bowl\n- Protein shake"

        tvDinnerItems.text =
            "- Veg biryani\n- Paneer curry + Roti\n- Mixed vegetable soup"

        tvWaterValue.text = "3 Litres per day"
    }

    private fun loadWeightLossDiet() {
        tvBreakfastItems.text =
            "- Poha / Upma\n- Fruit bowl\n- Green tea"

        tvLunchItems.text =
            "- 2 Roti + Sabzi + Dal\n- Brown rice bowl\n- Salad (cucumber, carrot)"

        tvSnacksItems.text =
            "- Nuts (almonds)\n- Coconut water\n- Apple or orange"

        tvDinnerItems.text =
            "- Light khichdi\n- Sprouts salad\n- Vegetable soup"

        tvWaterValue.text = "3.5 Litres per day"
    }

    private fun loadMaintainDiet() {
        tvBreakfastItems.text =
            "- Milk + Oats\n- Fruit bowl\n- Bread toast"

        tvLunchItems.text =
            "- Balanced thali (roti + sabzi + dal + rice)\n- Curd\n- Salad"

        tvSnacksItems.text =
            "- Fruits / Nuts\n- Tea (less sugar)"

        tvDinnerItems.text =
            "- Roti + Light sabzi\n- Dal rice\n- Veg soup"

        tvWaterValue.text = "3 Litres per day"
    }
}
