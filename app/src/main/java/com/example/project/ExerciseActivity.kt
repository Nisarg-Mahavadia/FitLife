package com.example.project

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ExerciseActivity : AppCompatActivity() {

    private lateinit var imgExerciseAnim: ImageView
    private lateinit var tvExerciseName: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvProgress: TextView
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private lateinit var btnExit: Button

    private var currentExercise = 0
    private var totalExercises = 0

    private var exerciseList = ArrayList<String>()
    private var exerciseTimes = ArrayList<String>()
    private var exerciseImages = ArrayList<String>()

    private var exerciseCategory = ""
    private var exerciseTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_page)

        // 🔙 Default Back Arrow
        supportActionBar?.title = "Exercise"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imgExerciseAnim = findViewById(R.id.imgExerciseAnim)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        tvTimer = findViewById(R.id.tvTimer)
        tvProgress = findViewById(R.id.tvProgress)
        btnNext = findViewById(R.id.btnNext)
        btnPrev = findViewById(R.id.btnPrev)
        btnExit = findViewById(R.id.btnExit)

        // Receive Data
        exerciseList = intent.getStringArrayListExtra("exerciseList") ?: arrayListOf()
        exerciseTimes = intent.getStringArrayListExtra("exerciseTimes") ?: arrayListOf()
        exerciseImages = intent.getStringArrayListExtra("exerciseImages") ?: arrayListOf()

        exerciseCategory = intent.getStringExtra("exerciseCategory") ?: "FullBody"
        currentExercise = intent.getIntExtra("currentExercise", 0)
        totalExercises = exerciseList.size

        showExercise(currentExercise)

        // Next Button
        btnNext.setOnClickListener {
            exerciseTimer?.cancel()
            navigateToTransition(currentExercise + 1)
        }

        // Previous Button
        btnPrev.setOnClickListener {
            exerciseTimer?.cancel()
            if (currentExercise == 0) goBackToCategory()
            else navigateToTransition(currentExercise - 1)
        }

        // Exit Button
        btnExit.setOnClickListener {
            exerciseTimer?.cancel()
            goBackToCategory()
        }
    }

    // 🔙 ActionBar back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            goBackToCategory()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToTransition(targetIndex: Int) {
        // ✔ IF last exercise finished → Go to completion page
        if (targetIndex >= totalExercises) {
            val intent = Intent(this, CompletionActivity::class.java)
            intent.putExtra("exerciseCategory", exerciseCategory)
            startActivity(intent)
            finish()
            return
        }

        // ✔ Otherwise → Normal transition
        val intent = Intent(this, TransitionActivity::class.java)
        intent.putStringArrayListExtra("exerciseList", exerciseList)
        intent.putStringArrayListExtra("exerciseTimes", exerciseTimes)
        intent.putStringArrayListExtra("exerciseImages", exerciseImages)
        intent.putExtra("exerciseCategory", exerciseCategory)
        intent.putExtra("currentExercise", targetIndex)
        startActivity(intent)
        finish()
    }

    private fun showExercise(index: Int) {
        if (index >= totalExercises) return

        tvExerciseName.text = exerciseList[index]
        tvProgress.text = "${index + 1} / $totalExercises"

        val imageName = exerciseImages[index]
        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        imgExerciseAnim.setImageResource(if (resId != 0) resId else R.drawable.ic_placeholder)

        // Disable previous on first exercise
        btnPrev.isEnabled = index != 0

        // Disable next on last exercise
        btnNext.isEnabled = index < totalExercises - 1

        val durationSec = exerciseTimes[index].trim().toIntOrNull() ?: 30
        val durationMillis = durationSec * 1000L

        exerciseTimer?.cancel()
        exerciseTimer = object : CountDownTimer(durationMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                // ⚠️ Auto move to next (or completion)
                navigateToTransition(index + 1)
            }
        }.start()
    }

    private fun goBackToCategory() {
        val intent = when (exerciseCategory) {
            "Arms" -> Intent(this, ArmsActivity::class.java)
            "Back" -> Intent(this, BackActivity::class.java)
            "Legs" -> Intent(this, LegsActivity::class.java)
            "Chest" -> Intent(this, ChestActivity::class.java)
            "Shoulders" -> Intent(this, ShouldersActivity::class.java)
            "Core" -> Intent(this, CoreActivity::class.java)
            else -> Intent(this, FullBodyActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        exerciseTimer?.cancel()
    }
}
