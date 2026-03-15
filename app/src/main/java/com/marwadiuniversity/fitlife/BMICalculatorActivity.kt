package com.marwadiuniversity.fitlife

import android.os.Bundle
import android.text.Html
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class BMICalculatorActivity : AppCompatActivity() {

    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var tvResult: TextView
    private lateinit var tvDietPlan: TextView
    private lateinit var dietPlanBox: LinearLayout
    private lateinit var btnCalculate: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_calculator)

        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        tvResult = findViewById(R.id.tvResult)
        tvDietPlan = findViewById(R.id.tvDietPlan)
        dietPlanBox = findViewById(R.id.dietPlanBox)
        btnCalculate = findViewById(R.id.btnCalculate)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnCalculate.setOnClickListener { calculateBMI() }
    }

    private fun calculateBMI() {
        val heightStr = etHeight.text.toString()
        val weightStr = etWeight.text.toString()

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show()
            return
        }

        val height = heightStr.toDouble() / 100
        val weight = weightStr.toDouble()
        val bmi = weight / (height * height)

        val df = DecimalFormat("#.##")
        val bmiFormatted = df.format(bmi)

        val category: String
        val dietPlan: String

        when {
            bmi < 18.5 -> {
                category = "Underweight"
                dietPlan = """ 
                    <b>🥗 Underweight Diet Plan</b><br><br>
                    <b>🍳 Breakfast:</b><br>
                    • Peanut butter toast / cheese sandwich<br>
                    • Banana or mango smoothie<br><br>
                    <b>🍱 Lunch:</b><br>
                    • Rice + dal + ghee<br>
                    • Paneer / chicken<br><br>
                    <b>🍽 Dinner:</b><br>
                    • Roti + paneer/chicken <br>
                    • Veg pulav + curd<br><br>
                    <b>✅ Tips:</b><br>
                    • Eat every 3 hours<br>
                    • Add nuts, dairy<br>
                """.trimIndent()
            }

            bmi in 18.5..24.9 -> {
                category = "Normal"
                dietPlan = """
                    <b>🍎 Balanced Diet Plan</b><br><br>
                    <b>🍳 Breakfast:</b><br>
                    • Oats + fruits<br>
                    • Poha/dosa<br><br>
                    <b>🍱 Lunch:</b><br>
                    • Roti + sabzi + dal<br>
                    • Brown rice<br><br>
                    <b>🍽 Dinner:</b><br>
                    • Light roti + sabzi<br>
                    • Grilled paneer/chicken<br><br>
                    <b>✅ Tips:</b><br>
                    • Drink 2–3L water<br>
                    • 30 min exercise<br>
                """.trimIndent()
            }

            bmi in 25.0..29.9 -> {
                category = "Overweight"
                dietPlan = """
                    <b>🥦 Overweight Diet Plan</b><br><br>
                    <b>🍳 Breakfast:</b><br>
                    • Oats / upma<br>
                    • Boiled eggs<br><br>
                    <b>🍱 Lunch:</b><br>
                    • Roti + sabzi + dal<br>
                    • Brown rice (small portion)<br><br>
                    <b>🍽 Dinner:</b><br>
                    • Soup + veggies<br>
                    • Avoid sugar<br><br>
                    <b>✅ Tips:</b><br>
                    • Reduce carbs after 7PM<br>
                    • 45 min cardio<br>
                """.trimIndent()
            }

            else -> {
                category = "Obese"
                dietPlan = """
                    <b>⚠️ Obese Diet Plan</b><br><br>
                    <b>🍳 Breakfast:</b><br>
                    • Oats / sprouts<br>
                    • Green tea<br><br>
                    <b>🍱 Lunch:</b><br>
                    • 1–2 roti + dal + sabzi<br>
                    • Large salad<br><br>
                    <b>🍽 Dinner:</b><br>
                    • Soup + grilled paneer/chicken<br>
                    • Avoid rice<br><br>
                    <b>✅ Tips:</b><br>
                    • No fried food<br>
                    • Walk 45–60 mins<br>
                """.trimIndent()
            }
        }

        tvResult.text = "Your BMI: $bmiFormatted\nCategory: $category"
        tvDietPlan.text = Html.fromHtml(dietPlan, Html.FROM_HTML_MODE_LEGACY)
        dietPlanBox.visibility = LinearLayout.VISIBLE

        // ✅ SAVE BMI TO FIRESTORE
        saveBMIToDatabase(bmiFormatted)
    }

    private fun saveBMIToDatabase(bmiFormatted: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .update("bmi", bmiFormatted)
            .addOnSuccessListener {
                Toast.makeText(this, "BMI saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save BMI!", Toast.LENGTH_SHORT).show()
            }
    }
}
