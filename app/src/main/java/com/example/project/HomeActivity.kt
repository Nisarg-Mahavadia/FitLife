package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvStatus: TextView
    private lateinit var bmiCircle: BMICircleView

    private lateinit var etTargetBMI: EditText
    private lateinit var btnSaveTarget: Button
    private lateinit var btnViewDiet: Button
    private lateinit var btnViewExercises: Button
    private lateinit var tvRecommended: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.title = "FitLife"

        tvWelcome = findViewById(R.id.tvWelcome)
        tvStatus = findViewById(R.id.tvStatus)
        bmiCircle = findViewById(R.id.bmiCircle)

        etTargetBMI = findViewById(R.id.etTargetBMI)
        btnSaveTarget = findViewById(R.id.btnSaveTarget)
        btnViewDiet = findViewById(R.id.btnViewDiet)
        btnViewExercises = findViewById(R.id.btnViewExercises)
        tvRecommended = findViewById(R.id.tvRecommended)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserData()
        handleSaveTargetBMI()

        btnViewDiet.setOnClickListener {
            startActivity(Intent(this, DietPlanActivity::class.java))
        }

        btnViewExercises.setOnClickListener {
            startActivity(Intent(this, WeeklyPlanActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivityForResult(Intent(this, ProfileActivity::class.java), 2001)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ⭐ Refresh BMI when returning from Profile/Edit Profile
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2001 && resultCode == RESULT_OK) {
            loadUserData()   // 🔥 Auto refresh BMI instantly
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->

                val name = doc.getString("name") ?: "User"
                val height = doc.get("height")?.toString()?.toFloatOrNull()
                val weight = doc.get("weight")?.toString()?.toFloatOrNull()
                val storedBMI = doc.get("bmi")?.toString()?.toFloatOrNull()

                tvWelcome.text = "Welcome, $name"

                // ✔ If BMI already stored → use it
                if (storedBMI != null) {
                    bmiCircle.setBMI(storedBMI)
                    tvStatus.text = "Status: ${getBMIStatus(storedBMI)}"
                    return@addOnSuccessListener
                }

                // ✔ New signup → calculate BMI once
                if (height != null && weight != null) {
                    val bmi = calculateBMI(height, weight)
                    bmiCircle.setBMI(bmi)
                    tvStatus.text = "Status: ${getBMIStatus(bmi)}"

                    db.collection("users")
                        .document(userId)
                        .update("bmi", String.format("%.1f", bmi))
                }
            }
    }

    private fun handleSaveTargetBMI() {
        btnSaveTarget.setOnClickListener {

            val target = etTargetBMI.text.toString().trim()
            if (target.isEmpty()) {
                etTargetBMI.error = "Enter a target BMI"
                return@setOnClickListener
            }

            val targetValue = target.toDoubleOrNull()
            if (targetValue == null || targetValue < 15 || targetValue > 35) {
                etTargetBMI.error = "Enter a valid BMI between 15 and 35"
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->

                    val height = doc.get("height")?.toString()?.toFloatOrNull()
                    val weight = doc.get("weight")?.toString()?.toFloatOrNull()

                    if (height == null || weight == null) return@addOnSuccessListener

                    val currentBMI = calculateBMI(height, weight)

                    val goalType = when {
                        targetValue > currentBMI -> "weight_gain"
                        targetValue < currentBMI -> "weight_loss"
                        else -> "maintain_weight"
                    }

                    db.collection("users").document(userId)
                        .update(
                            mapOf(
                                "target_bmi" to targetValue,
                                "goal_type" to goalType
                            )
                        )
                        .addOnSuccessListener {
                            tvRecommended.visibility = View.VISIBLE
                            btnViewDiet.visibility = View.VISIBLE
                            btnViewExercises.visibility = View.VISIBLE
                        }
                }
        }
    }

    private fun calculateBMI(heightCm: Float, weightKg: Float): Float {
        val h = heightCm / 100
        return weightKg / (h * h)
    }

    private fun getBMIStatus(bmi: Float): String =
        when {
            bmi < 18.5 -> "Underweight"
            bmi <= 24.9 -> "Normal"
            else -> "Overweight"
        }
}
