package com.afeka.gym.trainer

import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.afeka.gym.shared.model.Exercise
import com.afeka.gym.shared.ui.BaseWorkoutActivity

/**
 * Trainer side of the gym app. The trainer builds the workout plan: add new
 * exercises, edit their target sets/reps, or remove them. All of the list,
 * persistence and summary plumbing is inherited from [BaseWorkoutActivity];
 * this class only customises the four hooks.
 */
class TrainerActivity : BaseWorkoutActivity() {

    override fun screenTitle(): String = "Trainer · Workout Plan"

    override fun actionLabel(): String = "Add exercise"

    override fun emptyHint(): String = "Tap \"Add exercise\" to build the plan."

    override fun describeExercise(exercise: Exercise): String =
        "Target: ${exercise.targetSets} sets × ${exercise.targetReps} reps"

    /** The bottom button adds a brand-new exercise. */
    override fun onActionClicked() = showEditor(null, -1)

    /** Tapping a row edits (or deletes) that exercise. */
    override fun onExerciseClicked(exercise: Exercise, position: Int) =
        showEditor(exercise, position)

    private fun showEditor(existing: Exercise?, position: Int) {
        val nameField = EditText(this).apply {
            hint = "Exercise name"
            setText(existing?.name ?: "")
        }
        val setsField = EditText(this).apply {
            hint = "Target sets"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(existing?.targetSets?.toString() ?: "3")
        }
        val repsField = EditText(this).apply {
            hint = "Target reps"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(existing?.targetReps?.toString() ?: "10")
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
            addView(nameField)
            addView(setsField)
            addView(repsField)
        }

        val builder = AlertDialog.Builder(this)
            .setTitle(if (existing == null) "New exercise" else "Edit exercise")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val name = nameField.text.toString().trim().ifEmpty { "Exercise" }
                val sets = setsField.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: 1
                val reps = repsField.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: 1
                if (existing == null) {
                    exercises.add(Exercise(repository.nextId(), name, sets, reps))
                } else {
                    existing.name = name
                    existing.targetSets = sets
                    existing.targetReps = reps
                    // Don't let progress exceed the new target.
                    existing.completedSets = existing.completedSets.coerceAtMost(sets)
                }
                persist()
            }
            .setNegativeButton("Cancel", null)

        if (existing != null) {
            builder.setNeutralButton("Delete") { _, _ ->
                exercises.removeAt(position)
                persist()
            }
        }
        builder.show()
    }
}
