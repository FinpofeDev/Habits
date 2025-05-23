package com.finpofe.habits

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    //Instancia de FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() //Se guarda la referencia del splash
        super.onCreate(savedInstanceState)

        //Mantener splash abierto hasta que cargue
        splashScreen.setKeepOnScreenCondition { true }

        //Se inicializa auth
        auth = Firebase.auth

        //Referencia al usuario actual
        val currentUser = auth.currentUser

        //Splash abierto hasta que se cumpla esto
        lifecycleScope.launch {
            //Checa si hay usuario iniciado
            if (currentUser != null){
                //Se inicia Main si hay usuario iniciado
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }else{
                //Si no hay usuario inciado, manda a iniciar sesion
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }

            finish() //Finaliza la activity
        }
    }
}