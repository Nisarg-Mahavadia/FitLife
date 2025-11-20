package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ShouldersActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var linearLayoutExercises: LinearLayout

    private val exerciseList = ArrayList<String>()
    private val exerciseTimes = ArrayList<String>()
    private val exerciseImages = ArrayList<String>()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var userGender = "other" // male / female / other

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // 🔙 ENABLE BACK BUTTON
        supportActionBar?.title = "FitLife"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnStart = findViewById(R.id.btnStart)
        linearLayoutExercises = findViewById(R.id.linearLayoutExercises)

        loadUserGender()
    }

    private fun loadUserGender() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val genderStr = doc.getString("gender") ?: "Other"
                userGender = genderStr.lowercase()

                loadExercisesFromFirestore()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch user gender!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadExercisesFromFirestore() {

        firestore.collection("exercises")
            .document("shoulders")
            .collection("list")
            .orderBy("order")
            .get()
            .addOnSuccessListener { result ->

                exerciseList.clear()
                exerciseTimes.clear()
                exerciseImages.clear()
                linearLayoutExercises.removeAllViews()

                for (document in result) {

                    val name = document.getString("name") ?: continue

                    val applicable = document.get("applicable_for") as? List<*>
                        ?: listOf("male", "female")

                    // ❌ Skip if not suitable for user gender
                    if (!applicable.contains(userGender)) continue

                    val duration = when (userGender) {
                        "male" -> document.getString("male_duration") ?: "60"
                        "female" -> document.getString("female_duration") ?: "50"
                        else -> document.getString("male_duration") ?: "60"
                    }

                    val imageName = document.getString("imageURL") ?: "ic_placeholder"

                    exerciseList.add(name)
                    exerciseTimes.add(duration)
                    exerciseImages.add(imageName)

                    val itemView = layoutInflater.inflate(
                        R.layout.item_exercise,
                        linearLayoutExercises,
                        false
                    )

                    val nameView = itemView.findViewById<TextView>(R.id.tvExerciseName)
                    val timerView = itemView.findViewById<TextView>(R.id.tvTimer)
                    val imageView = itemView.findViewById<ImageView>(R.id.imgExercise)

                    nameView.text = name
                    timerView.text = "$duration sec"

                    val resId = resources.getIdentifier(imageName, "drawable", packageName)
                    imageView.setImageResource(if (resId != 0) resId else R.drawable.ic_placeholder)

                    linearLayoutExercises.addView(itemView)
                }

                btnStart.setOnClickListener {

                    if (exerciseList.isEmpty()) {
                        Toast.makeText(this, "No exercises for your gender!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val intent = Intent(this, ExerciseActivity::class.java)
                    intent.putStringArrayListExtra("exerciseList", exerciseList)
                    intent.putStringArrayListExtra("exerciseTimes", exerciseTimes)
                    intent.putStringArrayListExtra("exerciseImages", exerciseImages)
                    intent.putExtra("exerciseCategory", "Shoulders")
                    intent.putExtra("currentExercise", 0)

                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error loading Shoulders exercises", e)
                Toast.makeText(this, "Failed to load exercises", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔙 HANDLE BACK ARROW CLICK
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
