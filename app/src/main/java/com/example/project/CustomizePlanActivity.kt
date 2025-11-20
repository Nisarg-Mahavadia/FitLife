package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomizePlanActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    lateinit var spMon: Spinner
    lateinit var spTue: Spinner
    lateinit var spWed: Spinner
    lateinit var spThu: Spinner
    lateinit var spFri: Spinner
    lateinit var spSat: Spinner
    lateinit var spSun: Spinner

    private val options = listOf(
        "Full Body", "Arms", "Core", "Legs", "Chest + Back", "Shoulders", "Rest"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_plan)

        supportActionBar?.title = "Customize Plan"

        // ✅ Add Back Arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupSpinners()
        loadExistingPlan()

        findViewById<Button>(R.id.btnSaveCustomPlan).setOnClickListener {
            savePlan()
        }
    }

    // 🔙 Handle ActionBar Back Button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        spMon = findViewById(R.id.spMon)
        spTue = findViewById(R.id.spTue)
        spWed = findViewById(R.id.spWed)
        spThu = findViewById(R.id.spThu)
        spFri = findViewById(R.id.spFri)
        spSat = findViewById(R.id.spSat)
        spSun = findViewById(R.id.spSun)
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spMon.adapter = adapter
        spTue.adapter = adapter
        spWed.adapter = adapter
        spThu.adapter = adapter
        spFri.adapter = adapter
        spSat.adapter = adapter
        spSun.adapter = adapter
    }

    private fun loadExistingPlan() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val plan = doc.get("weekly_plan") as? Map<String, String> ?: return@addOnSuccessListener

                fun Spinner.setValue(text: String?) {
                    val index = options.indexOf(text)
                    if (index >= 0) setSelection(index)
                }

                spMon.setValue(plan["Monday"])
                spTue.setValue(plan["Tuesday"])
                spWed.setValue(plan["Wednesday"])
                spThu.setValue(plan["Thursday"])
                spFri.setValue(plan["Friday"])
                spSat.setValue(plan["Saturday"])
                spSun.setValue(plan["Sunday"])
            }
    }

    private fun savePlan() {
        val uid = auth.currentUser?.uid ?: return

        val plan = mapOf(
            "Monday" to spMon.selectedItem.toString(),
            "Tuesday" to spTue.selectedItem.toString(),
            "Wednesday" to spWed.selectedItem.toString(),
            "Thursday" to spThu.selectedItem.toString(),
            "Friday" to spFri.selectedItem.toString(),
            "Saturday" to spSat.selectedItem.toString(),
            "Sunday" to spSun.selectedItem.toString()
        )

        db.collection("users").document(uid)
            .update("weekly_plan", plan)
            .addOnSuccessListener {
                startActivity(Intent(this, WeeklyPlanActivity::class.java))
                finish()
            }
    }
}
