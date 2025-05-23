package com.finpofe.habits.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finpofe.habits.R

class HabitsAdapter(private val habit: List<Habit>) : RecyclerView.Adapter<HabitsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitsViewHolder, position: Int) {
        holder.render(habit[position])
    }

    override fun getItemCount() = habit.size
}