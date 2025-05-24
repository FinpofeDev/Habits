package com.finpofe.habits.fragments

import ObjFecha
import ObjLogros
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finpofe.habits.R
import com.finpofe.habits.objetos.AdapterLogros
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


class LogrosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterLogros
    private var listaLogros = mutableListOf<ObjLogros>()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var ref: DatabaseReference
    private var indiceSeleccionado: Int =-1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logros, container, false)

        recyclerView = view.findViewById(R.id.recyclerLogros)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        database = Firebase.database.reference

        //Asignar el adaptador
        adapter = AdapterLogros(listaLogros)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        //configurar el onItemClick del adaptador
        adapter.onItemClick = {posicion ->
            indiceSeleccionado = posicion
            Toast.makeText(requireContext(),"Seleccionado: ${listaLogros[posicion].nombre}",Toast.LENGTH_SHORT).show()
        }

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val userId = user?.uid

        ref = FirebaseDatabase.getInstance().getReference("users").child(userId?: "").child("logros")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaLogros.clear()
                for (child in snapshot.children) {
                    val logro = child.getValue(ObjLogros::class.java)
                    logro?.logroId = child.key
                    if (logro != null) {
                        listaLogros.add(logro)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error de lectura inicial", error.toException())
            }
        })

        val logrosListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val logro = snapshot.getValue(ObjLogros::class.java)
                logro?.logroId = snapshot.key  // Asegura que tenga su ID

                if (logro != null) {
                    // Opcional: evitar duplicados si ya está en la lista
                    if (listaLogros.none { it.logroId == logro.logroId }) {
                        listaLogros.add(logro)
                        adapter.notifyItemInserted(listaLogros.size - 1)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val logroActualizado = snapshot.getValue(ObjLogros::class.java)
                logroActualizado?.logroId = snapshot.key

                if (logroActualizado != null) {
                    val index = listaLogros.indexOfFirst { it.logroId == logroActualizado.logroId }
                    if (index != -1) {
                        listaLogros[index] = logroActualizado
                        adapter.notifyItemChanged(index)
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val logroEliminado = snapshot.getValue(ObjLogros::class.java)
                logroEliminado?.logroId = snapshot.key

                if (logroEliminado != null) {
                    val index = listaLogros.indexOfFirst { it.logroId == logroEliminado.logroId }
                    if (index != -1) {
                        listaLogros.removeAt(index)
                        adapter.notifyItemRemoved(index)
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
        ref.addChildEventListener(logrosListener)

        //botones
        val btnAdd = view.findViewById<ImageButton>(R.id.btnAdd)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        val btnCheck = view.findViewById<ImageButton>(R.id.btnCheck)

        btnAdd.setOnClickListener{

            // Inflar el layout personalizado
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nuevo_logro, null)

            val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
            val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
            val etRecompensa = dialogView.findViewById<EditText>(R.id.etRecompensa)
            val etFecha = dialogView.findViewById<EditText>(R.id.etFecha)
            val nuevoLogro = ObjLogros()

            // Crear el diálogo
            AlertDialog.Builder(requireContext())
                .setTitle("Nuevo Logro")
                .setView(dialogView)
                .setPositiveButton("Agregar") { _, _ ->
                    // Obtener los datos ingresados
                    val nombre = etNombre.text.toString()
                    val descripcion = etDescripcion.text.toString()
                    val recompensa = etRecompensa.text.toString()
                    val fechaTexto = etFecha.text.toString()

                    // Procesar fecha (formato simple dd/mm/aaaa)
                    val partes = fechaTexto.split("/")
                    if (partes.size == 3) {
                        val dia = partes[0].toIntOrNull() ?: 1
                        val mes = partes[1].toIntOrNull() ?: 1
                        val anio = partes[2].toIntOrNull() ?: 2025

                        val fecha = ObjFecha(dia, mes, anio)

                        // Crear el nuevo logro y agregarlo
                        nuevoLogro.nombre = nombre
                        nuevoLogro.descripcion =descripcion
                        nuevoLogro.recompensa = recompensa
                        nuevoLogro.fechaInicio = fecha // o setFechaFin(fecha)
                        val key = ref.push().key
                        nuevoLogro.logroId = key

                        ref.child(key!!).setValue(nuevoLogro)

                    } else {
                        Toast.makeText(requireContext(), "Fecha inválida", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()

        }

        btnDelete.setOnClickListener{

            if (indiceSeleccionado != -1) {
                val logroDelete =  listaLogros[indiceSeleccionado]

                val key = logroDelete.logroId
                if (key != null) {
                    ref.child(key).removeValue()
                } else {
                    Toast.makeText(requireContext(), "Error: ID de logro no encontrado", Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(requireContext(), "Logro eliminado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Selecciona un logro primero", Toast.LENGTH_SHORT).show()
            }
        }

        btnEdit.setOnClickListener{

            if (indiceSeleccionado != -1) {
                val logroUpdate = listaLogros[indiceSeleccionado]
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_editar_logro, null)

                val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
                val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
                val etRecompensa = dialogView.findViewById<EditText>(R.id.etRecompensa)
                val etDia = dialogView.findViewById<EditText>(R.id.etDia)
                val etMes = dialogView.findViewById<EditText>(R.id.etMes)
                val etYear = dialogView.findViewById<EditText>(R.id.etYear)

                // Rellenar campos con datos actuales
                etNombre.setText(logroUpdate.nombre)
                etDescripcion.setText(logroUpdate.descripcion)
                etRecompensa.setText(logroUpdate.recompensa)
                etDia.setText(logroUpdate.fechaInicio.dia.toString())
                etMes.setText(logroUpdate.fechaInicio.mes.toString())
                etYear.setText(logroUpdate.fechaInicio.anio.toString())

                // Crear diálogo
                AlertDialog.Builder(requireContext())
                    .setTitle("Editar Logro")
                    .setView(dialogView)
                    .setPositiveButton("Guardar") { _, _ ->
                        logroUpdate.nombre = etNombre.text.toString()
                        logroUpdate.descripcion = etDescripcion.text.toString()
                        logroUpdate.recompensa = etRecompensa.text.toString()
                        logroUpdate.fechaInicio = (
                            ObjFecha(
                                etDia.text.toString().toIntOrNull() ?: 1,
                                etMes.text.toString().toIntOrNull() ?: 1,
                                etYear.text.toString().toIntOrNull() ?: 2025
                            )
                        )

                        val logroValues = hashMapOf(
                            "nombre" to logroUpdate.nombre,
                            "descripcion" to logroUpdate.descripcion,
                            "recompensa" to logroUpdate.recompensa,
                            "fechaInicio" to mapOf(
                                "dia" to logroUpdate.fechaInicio.dia,
                                "mes" to logroUpdate.fechaInicio.mes,
                                "anio" to logroUpdate.fechaInicio.anio
                            ),
                            "fechaFin" to mapOf(
                                "dia" to logroUpdate.fechaFin.dia,
                                "mes" to logroUpdate.fechaFin.mes,
                                "anio" to logroUpdate.fechaFin.anio
                            ),
                            "completado" to logroUpdate.completado,
                            "logroId" to logroUpdate.logroId
                        )

                        val key = logroUpdate.logroId
                        if (key != null) {
                            ref.child(key).updateChildren(logroValues)
                        } else {
                            Toast.makeText(requireContext(), "Error: ID de logro no encontrado", Toast.LENGTH_SHORT).show()
                        }

                        Toast.makeText(requireContext(), "Logro actualizado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Selecciona un logro primero", Toast.LENGTH_SHORT).show()
            }

        }

        btnCheck.setOnClickListener{
            //Toast.makeText(requireContext(),"Marcar logro como",Toast.LENGTH_SHORT).show()

            if (indiceSeleccionado != -1) {
                val logroUpdate = listaLogros[indiceSeleccionado]
                logroUpdate.completado = true
                val logroValues = hashMapOf(
                    "nombre" to logroUpdate.nombre,
                    "descripcion" to logroUpdate.descripcion,
                    "recompensa" to logroUpdate.recompensa,
                    "fechaInicio" to mapOf(
                        "dia" to logroUpdate.fechaInicio.dia,
                        "mes" to logroUpdate.fechaInicio.mes,
                        "anio" to logroUpdate.fechaInicio.anio
                    ),
                    "fechaFin" to mapOf(
                        "dia" to logroUpdate.fechaFin.dia,
                        "mes" to logroUpdate.fechaFin.mes,
                        "anio" to logroUpdate.fechaFin.anio
                    ),
                    "completado" to logroUpdate.completado,
                    "logroId" to logroUpdate.logroId
                )

                val key = logroUpdate.logroId
                if (key != null) {
                    ref.child(key).updateChildren(logroValues)
                } else {
                    Toast.makeText(requireContext(), "Error: ID de logro no encontrado", Toast.LENGTH_SHORT).show()
                }

                // Deseleccionar
                indiceSeleccionado = -1

                Toast.makeText(requireContext(), "Logro marcado como completado y movido al final", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Selecciona un logro primero", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }



}

