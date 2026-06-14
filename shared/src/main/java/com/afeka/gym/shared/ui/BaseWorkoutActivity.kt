package com.afeka.gym.shared.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afeka.gym.shared.data.WorkoutRepository
import com.afeka.gym.shared.databinding.ActivityBaseWorkoutBinding
import com.afeka.gym.shared.model.Exercise

/**
 * Abstract screen shared by both applications. It owns everything common to
 * the trainer and trainee screens: loading the plan from [WorkoutRepository],
 * wiring the RecyclerView, the summary line, the action button and saving.
 *
 * Each app provides a concrete subclass that fills in the four abstract hooks
 * below. This is the "abstract Activity that the per-app Activities inherit
 * from" required by the assignment.
 */
abstract class BaseWorkoutActivity : AppCompatActivity() {

    protected lateinit var repository: WorkoutRepository
    protected val exercises = mutableListOf<Exercise>()

    private lateinit var binding: ActivityBaseWorkoutBinding
    private lateinit var adapter: ExerciseAdapter

    // ---- Hooks each app MUST implement ------------------------------------

    /** Title shown in the action bar. */
    protected abstract fun screenTitle(): String

    /** Label of the bottom action button. */
    protected abstract fun actionLabel(): String

    /** What happens when the bottom action button is tapped. */
    protected abstract fun onActionClicked()

    /** What happens when a row is tapped. */
    protected abstract fun onExerciseClicked(exercise: Exercise, position: Int)

    /** Secondary line shown under each exercise name. */
    protected abstract fun describeExercise(exercise: Exercise): String

    /** Optional hint shown when the plan is empty. */
    protected open fun emptyHint(): String = "No exercises yet."

    // ---- Common behaviour -------------------------------------------------

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = screenTitle()
        repository = WorkoutRepository(this)
        exercises.addAll(repository.load())

        adapter = ExerciseAdapter(
            items = exercises,
            describe = ::describeExercise,
            onClick = ::onExerciseClicked
        )
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        binding.actionButton.text = actionLabel()
        binding.actionButton.setOnClickListener { onActionClicked() }

        refresh()
    }

    /** Saves the current plan and redraws the list and summary. */
    protected fun persist() {
        repository.save(exercises)
        refresh()
    }

    /** Redraws the list, summary and empty-state without saving. */
    protected fun refresh() {
        adapter.notifyDataSetChanged()
        val done = exercises.count { it.isComplete }
        binding.summary.text =
            "${exercises.size} exercises · $done completed"
        val empty = exercises.isEmpty()
        binding.emptyHint.visibility = if (empty) View.VISIBLE else View.GONE
        binding.emptyHint.text = emptyHint()
        binding.recycler.visibility = if (empty) View.GONE else View.VISIBLE
    }
}
