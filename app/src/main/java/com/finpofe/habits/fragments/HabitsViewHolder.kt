package com.finpofe.habits.fragments

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finpofe.habits.R

class HabitsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val tVItemHabit: TextView = view.findViewById(R.id.tVItemHabit)
    fun render(habit: Habit) {
        tVItemHabit.text = habit.name
    }

}