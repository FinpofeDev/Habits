package com.finpofe.habits.objetos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.finpofe.habits.R

class HabitsAdapter(private val habit: List<Habit>) : RecyclerView.Adapter<HabitsAdapter.HabitsViewHolder>() {

    var onItemClick: ((Int) -> Unit)? = null

    inner class HabitsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre = itemView.findViewById<TextView>(R.id.tVItemHabit)

        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitsViewHolder, position: Int) {
        val habito = habit[position]
        holder.tvNombre.text = habito.name
    }

    override fun getItemCount() = habit.size
}