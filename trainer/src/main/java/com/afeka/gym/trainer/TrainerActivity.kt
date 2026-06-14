package com.afeka.gym.trainer

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.afeka.gym.shared.model.Exercise
import com.afeka.gym.shared.ui.BaseWorkoutActivity

/**
 * Trainer side of the gym app. The trainer builds the workout plan: add new
 * exercises, edit their target sets/reps, or delete them. The list,
 * persistence, theme and summary are all inherited from [BaseWorkoutActivity];
 * this class only customises the hooks and supplies the editor dialog.
 */
class TrainerActivity : BaseWorkoutActivity() {

    /** Which exercise the editor dialog is open for (null = closed). */
    private var editing by mutableStateOf<EditTarget?>(null)

    private data class EditTarget(val exercise: Exercise?, val index: Int)

    override fun screenTitle(): String = "Trainer · Workout Plan"

    override fun actionLabel(): String = "Add exercise"

    override fun emptyHint(): String = "Tap \"Add exercise\" to build the plan."

    override fun accentColor(): Color = Color(0xFF1565C0) // blue

    override fun describeExercise(exercise: Exercise): String =
        "Target: ${exercise.targetSets} sets × ${exercise.targetReps} reps"

    /** The bottom button opens the editor for a brand-new exercise. */
    override fun onActionClicked() {
        editing = EditTarget(null, -1)
    }

    /** Tapping a card opens the editor for that exercise. */
    override fun onExerciseClicked(exercise: Exercise, index: Int) {
        editing = EditTarget(exercise, index)
    }

    @Composable
    override fun OverlayContent() {
        val target = editing ?: return
        ExerciseEditorDialog(
            existing = target.exercise,
            onDismiss = { editing = null },
            onSave = { name, sets, reps ->
                val existing = target.exercise
                if (existing == null) {
                    exercises.add(Exercise(repository.nextId(), name, sets, reps))
                } else {
                    exercises[target.index] = existing.copy(
                        name = name,
                        targetSets = sets,
                        targetReps = reps,
                        completedSets = existing.completedSets.coerceAtMost(sets)
                    )
                }
                persist()
                editing = null
            },
            onDelete = if (target.exercise != null) {
                {
                    exercises.removeAt(target.index)
                    persist()
                    editing = null
                }
            } else null
        )
    }
}

@Composable
private fun ExerciseEditorDialog(
    existing: Exercise?,
    onDismiss: () -> Unit,
    onSave: (name: String, sets: Int, reps: Int) -> Unit,
    onDelete: (() -> Unit)?
) {
    var name by rememberSaveable(existing?.id) { mutableStateOf(existing?.name ?: "") }
    var sets by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.targetSets?.toString() ?: "3")
    }
    var reps by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.targetReps?.toString() ?: "10")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "New exercise" else "Edit exercise") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it.filter(Char::isDigit) },
                    label = { Text("Target sets") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter(Char::isDigit) },
                    label = { Text("Target reps") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val finalName = name.trim().ifEmpty { "Exercise" }
                val finalSets = sets.toIntOrNull()?.coerceAtLeast(1) ?: 1
                val finalReps = reps.toIntOrNull()?.coerceAtLeast(1) ?: 1
                onSave(finalName, finalSets, finalReps)
            }) { Text("Save") }
        },
        dismissButton = {
            if (onDelete != null) {
                TextButton(onClick = onDelete) { Text("Delete") }
            } else {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}
