package com.finpofe.habits.fragments

import ObjFecha
import ObjLogros
import android.app.AlertDialog
import android.media.Image
import android.os.Bundle
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


class LogrosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterLogros
    private lateinit var listaLogros: MutableList<ObjLogros>
    private var selectedLogroIndex: Int? = null
    private var indiceSeleccionado: Int =-1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logros, container, false)

        recyclerView = view.findViewById(R.id.recyclerLogros)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Lista para simular, despues de probar comentar las líneas :D
        listaLogros = mutableListOf(
            // Prueba inicial con datos de ejemplo
            ObjLogros("Aprender Kotlin", "Curso completo", "Certificado", ObjFecha(19, 5, 2025)),
            ObjLogros("Proyecto Android", "App de tareas", "10 puntos", ObjFecha(21, 5, 2025))
        )
        //Asignar el adaptador
        adapter = AdapterLogros(listaLogros)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        //configurar el onItemClick del adaptador
        adapter.onItemClick = {posicion ->
            indiceSeleccionado = posicion
            Toast.makeText(requireContext(),"Seleccionado: ${listaLogros[posicion].getNombre()}",Toast.LENGTH_SHORT).show()
        }

        //botones
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnAdd = view.findViewById<ImageButton>(R.id.btnAdd)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
        val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)
        val btnCheck = view.findViewById<ImageButton>(R.id.btnCheck)

        btnBack.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnAdd.setOnClickListener{
            //Toast.makeText(requireContext(),"Agregar logro",Toast.LENGTH_SHORT).show()

            // Inflar el layout personalizado
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nuevo_logro, null)

            val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
            val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
            val etRecompensa = dialogView.findViewById<EditText>(R.id.etRecompensa)
            val etFecha = dialogView.findViewById<EditText>(R.id.etFecha)

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
                        val nuevoLogro = ObjLogros()
                        nuevoLogro.setNombre(nombre)
                        nuevoLogro.setDescripcion(descripcion)
                        nuevoLogro.setRecompensa(recompensa)
                        nuevoLogro.setFechaInicio(fecha) // o setFechaFin(fecha)

                        listaLogros.add(nuevoLogro)
                        adapter.notifyItemInserted(listaLogros.size - 1)
                    } else {
                        Toast.makeText(requireContext(), "Fecha inválida", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()

        }

        btnDelete.setOnClickListener{
            //Toast.makeText(requireContext(), "Eliminar logro",Toast.LENGTH_SHORT).show()

            if (indiceSeleccionado != -1) {
                listaLogros.removeAt(indiceSeleccionado)
                adapter.notifyItemRemoved(indiceSeleccionado)
                indiceSeleccionado = -1
                Toast.makeText(requireContext(), "Logro eliminado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Selecciona un logro primero", Toast.LENGTH_SHORT).show()
            }
        }

        btnEdit.setOnClickListener{
            //Toast.makeText(requireContext(),"Editar logro",Toast.LENGTH_SHORT).show()

            if (indiceSeleccionado != -1) {
                val logro = listaLogros[indiceSeleccionado]
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_editar_logro, null)

                val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
                val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
                val etRecompensa = dialogView.findViewById<EditText>(R.id.etRecompensa)
                val etDia = dialogView.findViewById<EditText>(R.id.etDia)
                val etMes = dialogView.findViewById<EditText>(R.id.etMes)
                val etYear = dialogView.findViewById<EditText>(R.id.etYear)

                // Rellenar campos con datos actuales
                etNombre.setText(logro.getNombre())
                etDescripcion.setText(logro.getDescripcion())
                etRecompensa.setText(logro.getRecompensa())
                etDia.setText(logro.getFechaFin().getDia().toString())
                etMes.setText(logro.getFechaFin().getMes().toString())
                etYear.setText(logro.getFechaFin().getYear().toString())

                // Crear diálogo
                AlertDialog.Builder(requireContext())
                    .setTitle("Editar Logro")
                    .setView(dialogView)
                    .setPositiveButton("Guardar") { _, _ ->
                        logro.setNombre(etNombre.text.toString())
                        logro.setDescripcion(etDescripcion.text.toString())
                        logro.setRecompensa(etRecompensa.text.toString())
                        logro.setFechaFin(
                            ObjFecha(
                                etDia.text.toString().toIntOrNull() ?: 1,
                                etMes.text.toString().toIntOrNull() ?: 1,
                                etYear.text.toString().toIntOrNull() ?: 2025
                            )
                        )
                        adapter.notifyItemChanged(indiceSeleccionado)
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
                val logro = listaLogros[indiceSeleccionado]
                logro.marcarComoCompletado()

                // Eliminar de la posición actual
                listaLogros.removeAt(indiceSeleccionado)

                // Agregar al final
                listaLogros.add(logro)

                // Notificar al adapter
                adapter.notifyDataSetChanged()

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

class InterfazLogros {
    private val listaLogros : MutableList<ObjLogros> = mutableListOf()
    private var cantidadLogrosCompletados = 0
    private var cantidadLogrosIncompletos = 0

    fun mostrarLogros():String{
        return listaLogros.joinToString("\n"){it.toString()}
    }

    fun crearLogro(logro:ObjLogros):Boolean{
        val añadido = listaLogros.add(ObjLogros())
        if(añadido){
            cantidadLogrosCompletados++
        }
        return añadido
    }

    fun eliminarLogro(logro:ObjLogros):Boolean{
        val eliminado = listaLogros.remove(ObjLogros())
        if(eliminado){
            cantidadLogrosIncompletos--
        }
        return eliminado
    }

    fun editarLogro(logro:ObjLogros):Boolean{
        val index = listaLogros.indexOfFirst { it.getNombre() == logro.getNombre() }
        return if(index != -1){
            listaLogros[index] = logro
            true
        } else{
            false
        }
    }

    fun marcarLogroCompletado(logro: ObjLogros): Boolean{
        val index = listaLogros.indexOf(logro)
        if(index != -1){
            cantidadLogrosCompletados++
            cantidadLogrosIncompletos--
            return true
        }
        return false
    }

    fun marcarLogroNoCompletado(logro: ObjLogros): Boolean{
        val index = listaLogros.indexOf(logro)
        if(index != -1){
            cantidadLogrosCompletados--
            cantidadLogrosIncompletos++
            return  true
        }
        return false
    }
}


