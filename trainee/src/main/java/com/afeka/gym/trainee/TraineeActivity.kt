package com.afeka.gym.trainee

import android.widget.Toast
import com.afeka.gym.shared.model.Exercise
import com.afeka.gym.shared.ui.BaseWorkoutActivity

/**
 * Trainee side of the gym app. The trainee performs the plan: tapping a row
 * logs one more completed set; the bottom button resets all progress. The
 * exact same [BaseWorkoutActivity] is reused — only the four hooks differ
 * from the trainer's, which is what makes the inheritance worthwhile.
 */
class TraineeActivity : BaseWorkoutActivity() {

    override fun screenTitle(): String = "Trainee · My Workout"

    override fun actionLabel(): String = "Reset progress"

    override fun emptyHint(): String = "Your trainer hasn't added exercises yet."

    override fun describeExercise(exercise: Exercise): String =
        "Done ${exercise.completedSets}/${exercise.targetSets} sets · " +
            "${exercise.targetReps} reps each"

    /** The bottom button clears all logged sets so the workout can restart. */
    override fun onActionClicked() {
        exercises.forEach { it.completedSets = 0 }
        persist()
        Toast.makeText(this, "Progress reset", Toast.LENGTH_SHORT).show()
    }

    /** Tapping a row logs one more completed set (and wraps back to 0 when full). */
    override fun onExerciseClicked(exercise: Exercise, position: Int) {
        exercise.completedSets =
            if (exercise.completedSets >= exercise.targetSets) 0
            else exercise.completedSets + 1
        persist()
        if (exercise.isComplete) {
            Toast.makeText(this, "${exercise.name} complete! 💪", Toast.LENGTH_SHORT).show()
        }
    }
}
