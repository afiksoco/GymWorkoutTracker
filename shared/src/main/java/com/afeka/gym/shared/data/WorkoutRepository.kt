package com.afeka.gym.shared.data

import android.content.Context
import com.afeka.gym.shared.model.Exercise
import org.json.JSONArray

/**
 * Persists the workout plan in [android.content.SharedPreferences] as a JSON
 * array. Both apps use this same repository class; each app keeps its own copy
 * on the device (apps run in separate sandboxes), seeded with the same default
 * plan so the two screens look coherent in the demo.
 */
class WorkoutRepository(context: Context) {

    private val prefs =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): MutableList<Exercise> {
        val raw = prefs.getString(KEY_PLAN, null) ?: return defaultPlan()
        return try {
            val array = JSONArray(raw)
            MutableList(array.length()) { i -> Exercise.fromJson(array.getJSONObject(i)) }
        } catch (e: Exception) {
            defaultPlan()
        }
    }

    fun save(exercises: List<Exercise>) {
        val array = JSONArray()
        exercises.forEach { array.put(it.toJson()) }
        prefs.edit().putString(KEY_PLAN, array.toString()).apply()
    }

    fun nextId(): String = "ex-" + (prefs.getInt(KEY_SEQ, 0) + 1).also {
        prefs.edit().putInt(KEY_SEQ, it).apply()
    }

    private fun defaultPlan(): MutableList<Exercise> = mutableListOf(
        Exercise("ex-1", "Squats", targetSets = 4, targetReps = 10),
        Exercise("ex-2", "Bench Press", targetSets = 3, targetReps = 8),
        Exercise("ex-3", "Deadlift", targetSets = 3, targetReps = 5),
        Exercise("ex-4", "Pull-ups", targetSets = 3, targetReps = 12)
    )

    private companion object {
        const val PREFS_NAME = "gym_workout_plan"
        const val KEY_PLAN = "plan_json"
        const val KEY_SEQ = "id_sequence"
    }
}
