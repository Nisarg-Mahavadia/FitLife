package com.marwadiuniversity.fitlife

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AnalysisFragment : Fragment(R.layout.fragment_analysis) {

    private lateinit var tvWelcome: TextView
    private lateinit var tvStatus: TextView
    private lateinit var bmiCircle: BMICircleView

    private lateinit var etTargetWeight: EditText
    private lateinit var btnSaveTargetWeight: Button
    private lateinit var btnViewDiet: LinearLayout
    private lateinit var btnViewExercises: LinearLayout
    private lateinit var tvRecommended: TextView

    private lateinit var switchGoal: Switch
    private lateinit var tvGoalLabel: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var currentWeight: Float? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvWelcome = view.findViewById(R.id.tvWelcome)
        tvStatus = view.findViewById(R.id.tvStatus)
        bmiCircle = view.findViewById(R.id.bmiCircle)

        etTargetWeight = view.findViewById(R.id.etTargetBMI)
        btnSaveTargetWeight = view.findViewById(R.id.btnSaveTarget)
        btnViewDiet = view.findViewById(R.id.btnViewDiet)
        btnViewExercises = view.findViewById(R.id.btnViewExercises)
        tvRecommended = view.findViewById(R.id.tvRecommended)

        switchGoal = view.findViewById(R.id.switchGoal)
        tvGoalLabel = view.findViewById(R.id.tvGoalLabel)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserData()
        handleGoalSwitch()
        handleSaveTargetWeight()

        btnViewDiet.setOnClickListener {
            startActivity(Intent(requireContext(), DietPlanActivity::class.java))
        }

        btnViewExercises.setOnClickListener {
            startActivity(Intent(requireContext(), WeeklyPlanActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // When coming back from profile, refresh data
        loadUserData()
    }

    // ---------- Firestore helpers ----------

    private fun getFloatSafe(doc: Map<String, Any>, key: String): Float? {
        val value = doc[key] ?: return null
        return when (value) {
            is Number -> value.toFloat()
            is String -> value.toFloatOrNull()
            else -> null
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->

                val data = document.data ?: return@addOnSuccessListener

                val name = data["name"]?.toString() ?: "User"
                val height = getFloatSafe(data, "height")
                val weight = getFloatSafe(data, "weight")
                val storedBMI = getFloatSafe(data, "bmi")

                tvWelcome.text = "Welcome, $name"

                currentWeight = weight

                if (weight != null) {
                    etTargetWeight.hint =
                        "Enter Target Weight (Currently: ${weight.toInt()} kg)"
                }

                if (storedBMI != null) {
                    bmiCircle.setBMI(storedBMI)
                    tvStatus.text = "Status: ${getBMIStatus(storedBMI)}"
                    tvStatus.setTextColor(getStatusColor(storedBMI))
                    return@addOnSuccessListener
                }

                if (height != null && weight != null) {
                    val bmi = calculateBMI(height, weight)
                    bmiCircle.setBMI(bmi)

                    tvStatus.text = "Status: ${getBMIStatus(bmi)}"
                    tvStatus.setTextColor(getStatusColor(bmi))

                    db.collection("users")
                        .document(userId)
                        .update("bmi", String.format("%.1f", bmi))
                }
            }
    }

    private fun handleGoalSwitch() {
        switchGoal.setOnCheckedChangeListener { _, isChecked ->
            tvGoalLabel.text =
                if (isChecked) "Goal: Weight Gain" else "Goal: Weight Loss"
        }
    }

    private fun handleSaveTargetWeight() {

        btnSaveTargetWeight.setOnClickListener {

            val targetStr = etTargetWeight.text.toString().trim()

            if (targetStr.isEmpty()) {
                etTargetWeight.error = "Enter target weight (kg)"
                return@setOnClickListener
            }

            val targetKg = targetStr.toFloatOrNull()
            if (targetKg == null || targetKg !in 20f..200f) {
                etTargetWeight.error = "Enter valid weight (20–200 kg)"
                return@setOnClickListener
            }

            val current = currentWeight
            if (current == null) {
                etTargetWeight.error = "Current weight not loaded"
                return@setOnClickListener
            }

            val isGain = switchGoal.isChecked

            if (isGain && targetKg <= current) {
                etTargetWeight.error =
                    "Target must be MORE than current weight (${current.toInt()} kg)"
                return@setOnClickListener
            }

            if (!isGain && targetKg >= current) {
                etTargetWeight.error =
                    "Target must be LESS than current weight (${current.toInt()} kg)"
                return@setOnClickListener
            }

            val goalType = if (isGain) "weight_gain" else "weight_loss"
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            db.collection("users").document(userId)
                .update(
                    mapOf(
                        "target_weight" to targetKg,
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

    private fun calculateBMI(heightCm: Float, weightKg: Float): Float {
        val h = heightCm / 100f
        return weightKg / (h * h)
    }

    private fun getBMIStatus(bmi: Float): String =
        when {
            bmi < 18.5 -> "Underweight"
            bmi <= 24.9 -> "Normal"
            else -> "Overweight"
        }

    private fun getStatusColor(bmi: Float): Int {
        return when {
            bmi < 18.5 ->
                ContextCompat.getColor(requireContext(), R.color.bmi_underweight)
            bmi <= 24.9 ->
                ContextCompat.getColor(requireContext(), R.color.bmi_normal)
            else ->
                ContextCompat.getColor(requireContext(), R.color.bmi_overweight)
        }
    }
}
