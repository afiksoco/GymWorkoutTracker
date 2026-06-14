package com.afeka.gym.shared.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afeka.gym.shared.model.Exercise
import com.afeka.gym.shared.databinding.ItemExerciseBinding

/**
 * Shared RecyclerView adapter used by both apps. The text of the secondary
 * line and the click behaviour are supplied by whichever Activity owns it,
 * so the same list renders differently for the trainer and the trainee.
 */
class ExerciseAdapter(
    private val items: List<Exercise>,
    private val describe: (Exercise) -> String,
    private val onClick: (Exercise, Int) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.VH>() {

    inner class VH(val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val exercise = items[position]
        holder.binding.exerciseName.text = exercise.name
        holder.binding.exerciseDetail.text = describe(exercise)
        holder.binding.completeBadge.visibility =
            if (exercise.isComplete) android.view.View.VISIBLE else android.view.View.GONE
        holder.binding.root.setOnClickListener {
            onClick(exercise, holder.bindingAdapterPosition)
        }
    }
}
