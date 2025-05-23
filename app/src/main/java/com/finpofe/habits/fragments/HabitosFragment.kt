package com.finpofe.habits.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finpofe.habits.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HabitosFragment : Fragment() {

    private val habits = mutableListOf(
        Habit(name = "Prueba")
    )

    private lateinit var rVHabits: RecyclerView
    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var efabAddHabits: ExtendedFloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_habitos, container, false)
        initComponent(view)
        initUI()
        initListeners()

        return view
    }

    private fun initListeners() {
        efabAddHabits.setOnClickListener {  }
    }

    private fun showDialog(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_habit)
        val btnAddHabit: Button = dialog.findViewById(R.id.btnAddHabit)
        val eTHabit: EditText = dialog.findViewById(R.id.eTHabit)

        btnAddHabit.setOnClickListener {
            val currentHabit = eTHabit.text.toString()
            if (currentHabit.isNotEmpty()){
                habits.add(Habit(currentHabit))
                updateHabits()
                //dialog.hide()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun initComponent(view: View) {
        rVHabits = view.findViewById(R.id.rVHabits)
        efabAddHabits = view.findViewById(R.id.efabAddHabits)
    }

    private fun initUI() {
        habitsAdapter = HabitsAdapter(habits)
        rVHabits.layoutManager = LinearLayoutManager(requireContext())
        rVHabits.adapter = habitsAdapter
    }

    private fun updateHabits(){
        habitsAdapter.notifyDataSetChanged()
    }
}
