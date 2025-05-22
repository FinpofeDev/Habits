package com.finpofe.habits.fragments

import ObjLogros
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finpofe.habits.R


class LogrosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_logros, container, false)
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