package com.afeka.gym.shared.model

import org.json.JSONObject

/**
 * A single exercise in a workout plan. Shared by both apps:
 *  - the Trainer app sets [name], [targetSets] and [targetReps];
 *  - the Trainee app updates [completedSets] as the workout is performed.
 */
data class Exercise(
    val id: String,
    var name: String,
    var targetSets: Int,
    var targetReps: Int,
    var completedSets: Int = 0
) {
    /** True once the trainee has logged every target set. */
    val isComplete: Boolean
        get() = completedSets >= targetSets

    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("targetSets", targetSets)
        put("targetReps", targetReps)
        put("completedSets", completedSets)
    }

    companion object {
        fun fromJson(obj: JSONObject): Exercise = Exercise(
            id = obj.getString("id"),
            name = obj.getString("name"),
            targetSets = obj.getInt("targetSets"),
            targetReps = obj.getInt("targetReps"),
            completedSets = obj.optInt("completedSets", 0)
        )
    }
}
