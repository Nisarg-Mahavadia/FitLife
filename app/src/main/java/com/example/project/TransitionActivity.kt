package com.example.project

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TransitionActivity : AppCompatActivity() {

    private lateinit var tvNextExerciseTitle: TextView
    private lateinit var tvNextExerciseName: TextView
    private lateinit var tvRestTimer: TextView
    private lateinit var imgNextExercise: ImageView

    private var currentExercise = 0
    private var totalExercises = 0

    private var exerciseList = ArrayList<String>()
    private var exerciseTimes = ArrayList<String>()
    private var exerciseImages = ArrayList<String>()

    private var exerciseCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)

        tvNextExerciseTitle = findViewById(R.id.tvNextExerciseTitle)
        tvNextExerciseName = findViewById(R.id.tvNextExerciseName)
        tvRestTimer = findViewById(R.id.tvRestTimer)
        imgNextExercise = findViewById(R.id.imgNextExercise)

        exerciseList = intent.getStringArrayListExtra("exerciseList") ?: arrayListOf()
        exerciseTimes = intent.getStringArrayListExtra("exerciseTimes") ?: arrayListOf()
        exerciseImages = intent.getStringArrayListExtra("exerciseImages") ?: arrayListOf()

        exerciseCategory = intent.getStringExtra("exerciseCategory") ?: "FullBody"
        currentExercise = intent.getIntExtra("currentExercise", 0)
        totalExercises = exerciseList.size

        // ⭐ CASE 1 → Last exercise finished → Go to CompletionActivity
        if (currentExercise >= totalExercises) {
            goToCompletion()
            return
        }

        // ⭐ CASE 2 → Show next exercise preview
        tvNextExerciseName.text = exerciseList[currentExercise]
        val imageName = exerciseImages[currentExercise]
        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        imgNextExercise.setImageResource(if (resId != 0) resId else R.drawable.ic_placeholder)

        // ⭐ 10-second rest timer
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvRestTimer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                val intent = Intent(this@TransitionActivity, ExerciseActivity::class.java)
                intent.putStringArrayListExtra("exerciseList", exerciseList)
                intent.putStringArrayListExtra("exerciseTimes", exerciseTimes)
                intent.putStringArrayListExtra("exerciseImages", exerciseImages)
                intent.putExtra("exerciseCategory", exerciseCategory)
                intent.putExtra("currentExercise", currentExercise)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    private fun goToCompletion() {
        val intent = Intent(this, CompletionActivity::class.java)
        intent.putExtra("exerciseCategory", exerciseCategory)
        startActivity(intent)
        finish()
    }
}
