package com.marwadiuniversity.fitlife

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class DailyTasksFragment : Fragment() {

    private lateinit var taskManager: DailyTaskManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskManager = DailyTaskManager(requireContext())
        taskManager.resetIfNewDay()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_daily_tasks, container, false)

        val btnExerciseDone: Button = view.findViewById(R.id.btnExerciseDone)
        val btnDietDone: Button = view.findViewById(R.id.btnDietDone)
        val btnWaterDone: Button = view.findViewById(R.id.btnWaterDone)
        val btnSleepDone: Button = view.findViewById(R.id.btnSleepDone)
        val btnStepsDone: Button = view.findViewById(R.id.btnStepsDone)

        setButtonState("EXERCISE", btnExerciseDone)
        setButtonState("DIET", btnDietDone)
        setButtonState("WATER", btnWaterDone)
        setButtonState("SLEEP", btnSleepDone)
        setButtonState("STEPS", btnStepsDone)

        btnExerciseDone.setOnClickListener {
            markDone("EXERCISE", btnExerciseDone, "Workout completed!")
        }

        btnDietDone.setOnClickListener {
            markDone("DIET", btnDietDone, "Diet completed!")
        }

        btnWaterDone.setOnClickListener {
            markDone("WATER", btnWaterDone, "Water goal completed!")
        }

        btnSleepDone.setOnClickListener {
            markDone("SLEEP", btnSleepDone, "Sleep goal completed!")
        }

        btnStepsDone.setOnClickListener {
            markDone("STEPS", btnStepsDone, "Steps goal completed!")
        }

        return view
    }

    private fun markDone(task: String, button: Button, message: String) {
        taskManager.markTaskCompleted(task)
        button.text = "COMPLETED ✔"
        button.isEnabled = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setButtonState(task: String, button: Button) {
        if (taskManager.isTaskCompleted(task)) {
            button.text = "COMPLETED ✔"
            button.isEnabled = false
        }
    }
}
