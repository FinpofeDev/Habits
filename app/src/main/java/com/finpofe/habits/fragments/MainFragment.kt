package com.finpofe.habits.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.finpofe.habits.R
import com.finpofe.habits.objetos.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var bienvenidatxt: TextView
    private var nombreUsuario: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        iniciarVariables(view)

        return view
    }

    private fun iniciarVariables(view: View){
        auth = Firebase.auth
        bienvenidatxt = view.findViewById(R.id.bienvenida_lbl)

        val user = Firebase.auth.currentUser

        nombreUsuario = user?.displayName
        bienvenidatxt.text = getString(R.string.bienvenida, nombreUsuario?: "Invitado")

    }
}