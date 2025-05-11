package com.finpofe.habits

import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() //Se guarda la referencia del splash
        super.onCreate(savedInstanceState)

        //Mantener splash abierto hasta que este listo lo demas
        splashScreen.setKeepOnScreenCondition { true }

        //Splash abierto hasta que se cumpla esto
        lifecycleScope.launch{
            delay(2000) //En lugar de delay, cargar datos xd
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java)) //Se inicia la Main
            finish() //Finaliza la activity
        }
    }
}