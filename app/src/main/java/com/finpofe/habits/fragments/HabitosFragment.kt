package com.finpofe.habits.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finpofe.habits.R
import com.finpofe.habits.objetos.Habit
import com.finpofe.habits.objetos.HabitsAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HabitosFragment : Fragment() {

    private var habits = mutableListOf<Habit>()

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    private lateinit var rVHabits: RecyclerView
    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var efabAddHabits: ExtendedFloatingActionButton
    private lateinit var efaEditHabits: ExtendedFloatingActionButton
    private lateinit var efaDeleteHabits: ExtendedFloatingActionButton

    private var indiceSeleccionado: Int =-1

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
        efabAddHabits.setOnClickListener   { showDialogAdd()  }

        efaEditHabits.setOnClickListener   { showDialogEdit() }

        efaDeleteHabits.setOnClickListener { deleteHabit() }

        //configurar el onItemClick del adaptador
        habitsAdapter.onItemClick = { posicion ->
            indiceSeleccionado = posicion
            Toast.makeText(requireContext(),"Seleccionado: ${habits[posicion].name}", Toast.LENGTH_SHORT).show()
        }

        val habitosListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val habit = snapshot.getValue(Habit::class.java)
                habit?.habitoId = snapshot.key  // Asegura que tenga su ID

                if (habit != null) {
                    // Opcional: evitar duplicados si ya está en la lista
                    if (habits.none { it.habitoId == habit.habitoId }) {
                        habits.add(habit)
                        habitsAdapter.notifyItemInserted(habits.size - 1)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val habitoActualizado = snapshot.getValue(Habit::class.java)
                habitoActualizado?.habitoId = snapshot.key

                if (habitoActualizado != null) {
                    val index = habits.indexOfFirst { it.habitoId == habitoActualizado.habitoId }
                    if (index != -1) {
                        habits[index] = habitoActualizado
                        habitsAdapter.notifyItemChanged(index)
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val habitoEliminado = snapshot.getValue(Habit::class.java)
                habitoEliminado?.habitoId = snapshot.key

                if (habitoEliminado != null) {
                    val index = habits.indexOfFirst { it.habitoId == habitoEliminado.habitoId }
                    if (index != -1) {
                        habits.removeAt(index)
                        habitsAdapter.notifyItemRemoved(index)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Rara vez es necesario, puedes dejarlo vacío
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al escuchar logros", error.toException())
            }
        }
        ref.addChildEventListener(habitosListener)

    }

    private fun showDialogAdd() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_habit)
        val btnAddHabit: Button = dialog.findViewById(R.id.btnAddHabit)
        val eTHabit: EditText = dialog.findViewById(R.id.eTHabit)

        btnAddHabit.setOnClickListener {
            val nombre = eTHabit.text.toString()
            if (nombre.isNotEmpty()){
                val nuevoHabito = Habit()
                nuevoHabito.name = nombre
                val key = ref.push().key
                nuevoHabito.habitoId = key

                ref.child(key!!).setValue(nuevoHabito)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDialogEdit() {
        if(indiceSeleccionado != -1) {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_habit)
            val btnAddHabit: Button = dialog.findViewById(R.id.btnAddHabit)
            val eTHabit: EditText = dialog.findViewById(R.id.eTHabit)

            btnAddHabit.text = "Guardar"
            val habitoUpdate = habits[indiceSeleccionado]

            btnAddHabit.setOnClickListener {
                val nombre = eTHabit.text.toString()
                if (nombre.isNotEmpty()){

                    habitoUpdate.name = nombre
                    val habitoValues = mapOf(
                        "habitoId" to habitoUpdate.habitoId,
                        "name" to habitoUpdate.name
                    )

                    val key = habitoUpdate.habitoId
                    if (key != null) {
                        ref.child(key).updateChildren(habitoValues)
                    } else {
                        Toast.makeText(requireContext(), "Error: ID de habito no encontrado", Toast.LENGTH_SHORT).show()
                    }

                    dialog.dismiss()
                }
            }

            dialog.show()
        }else {
            Toast.makeText(requireContext(), "Selecciona un habito primero", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteHabit() {
        if (indiceSeleccionado != -1) {
            val habitoDelete =  habits[indiceSeleccionado]

            val key = habitoDelete.habitoId
            if (key != null) {
                ref.child(key).removeValue()
            } else {
                Toast.makeText(requireContext(), "Error: ID de habito no encontrado", Toast.LENGTH_SHORT).show()
            }

            Toast.makeText(requireContext(), "habito eliminado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Selecciona un habito primero", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initComponent(view: View) {
        rVHabits = view.findViewById(R.id.rVHabits)
        efabAddHabits = view.findViewById(R.id.efabAddHabits)
        efaEditHabits = view.findViewById(R.id.efabEditHabits)
        efaDeleteHabits = view.findViewById(R.id.efabDeleteHabits)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val userId = user?.uid

        ref = FirebaseDatabase.getInstance().getReference("users").child(userId?: "").child("habitos")
    }

    private fun initUI() {
        habitsAdapter = HabitsAdapter(habits)
        rVHabits.layoutManager = LinearLayoutManager(requireContext())
        rVHabits.adapter = habitsAdapter

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                habits.clear()
                for (child in snapshot.children) {
                    val habit = child.getValue(Habit::class.java)
                    habit?.habitoId = child.key
                    if (habit != null) {
                        habits.add(habit)
                    }
                }
                habitsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error de lectura inicial", error.toException())
            }
        })
    }

    private fun updateHabits(){
        habitsAdapter.notifyDataSetChanged()
    }
}
