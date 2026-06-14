package com.afeka.gym.trainee

import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.afeka.gym.shared.model.Exercise
import com.afeka.gym.shared.ui.BaseWorkoutActivity

/**
 * Trainee side of the gym app. The trainee performs the plan: tapping a card
 * logs one more completed set; the bottom button resets all progress. The
 * exact same [BaseWorkoutActivity] is reused — only the hooks differ from the
 * trainer's, which is what makes the inheritance worthwhile.
 */
class TraineeActivity : BaseWorkoutActivity() {

    override fun screenTitle(): String = "Trainee · My Workout"

    override fun actionLabel(): String = "Reset progress"

    override fun emptyHint(): String = "Your trainer hasn't added exercises yet."

    override fun accentColor(): Color = Color(0xFF2E7D32) // green

    override fun describeExercise(exercise: Exercise): String =
        "Done ${exercise.completedSets}/${exercise.targetSets} sets · " +
            "${exercise.targetReps} reps each"

    /** The bottom button clears all logged sets so the workout can restart. */
    override fun onActionClicked() {
        for (i in exercises.indices) {
            exercises[i] = exercises[i].copy(completedSets = 0)
        }
        persist()
        Toast.makeText(this, "Progress reset", Toast.LENGTH_SHORT).show()
    }

    /** Tapping a card logs one more set (and wraps back to 0 once full). */
    override fun onExerciseClicked(exercise: Exercise, index: Int) {
        val next =
            if (exercise.completedSets >= exercise.targetSets) 0
            else exercise.completedSets + 1
        val updated = exercise.copy(completedSets = next)
        exercises[index] = updated
        persist()
        if (updated.isComplete) {
            Toast.makeText(this, "${updated.name} complete! 💪", Toast.LENGTH_SHORT).show()
        }
    }
}
