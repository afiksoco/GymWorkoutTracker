package com.afeka.gym.shared.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.afeka.gym.shared.data.WorkoutRepository
import com.afeka.gym.shared.model.Exercise

/**
 * Abstract Compose screen shared by both applications. It owns everything
 * common to the trainer and trainee: the observable plan state, loading and
 * saving via [WorkoutRepository], the theme and the shared [WorkoutScreen].
 *
 * Each app provides a concrete subclass that fills in the abstract hooks
 * below. This is the "abstract Activity that the per-app Activities inherit
 * from" required by the assignment — now expressed with Jetpack Compose.
 */
abstract class BaseWorkoutActivity : ComponentActivity() {

    protected lateinit var repository: WorkoutRepository

    /**
     * The plan as observable Compose state. Replace items with `copy(...)`
     * (e.g. `exercises[i] = exercises[i].copy(...)`) so recomposition fires.
     */
    protected val exercises = mutableStateListOf<Exercise>()

    // ---- Hooks each app MUST implement ------------------------------------

    /** Title shown in the top app bar. */
    protected abstract fun screenTitle(): String

    /** Label of the bottom action button. */
    protected abstract fun actionLabel(): String

    /** What happens when the bottom action button is tapped. */
    protected abstract fun onActionClicked()

    /** What happens when a card is tapped. */
    protected abstract fun onExerciseClicked(exercise: Exercise, index: Int)

    /** Secondary line shown under each exercise name. */
    protected abstract fun describeExercise(exercise: Exercise): String

    // ---- Hooks an app MAY override ----------------------------------------

    /** Hint shown when the plan is empty. */
    protected open fun emptyHint(): String = "No exercises yet."

    /** App accent colour (blue trainer / green trainee). */
    protected open fun accentColor(): Color = Color(0xFF1565C0)

    /**
     * Extra UI layered on top of the screen — used by the trainer app to show
     * its add/edit dialog. Default: nothing.
     */
    @Composable
    protected open fun OverlayContent() {
    }

    // ---- Common behaviour -------------------------------------------------

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = WorkoutRepository(this)
        exercises.addAll(repository.load())

        setContent {
            WorkoutTheme(accent = accentColor()) {
                val done = exercises.count { it.isComplete }
                WorkoutScreen(
                    title = screenTitle(),
                    summary = "${exercises.size} exercises · $done completed",
                    exercises = exercises,
                    describe = ::describeExercise,
                    actionLabel = actionLabel(),
                    emptyHint = emptyHint(),
                    onAction = ::onActionClicked,
                    onExerciseClick = ::onExerciseClicked
                )
                OverlayContent()
            }
        }
    }

    /** Persists the current plan. The UI updates automatically from state. */
    protected fun persist() {
        repository.save(exercises)
    }
}
