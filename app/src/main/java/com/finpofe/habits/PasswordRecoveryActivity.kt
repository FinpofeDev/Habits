package com.finpofe.habits

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class PasswordRecoveryActivity : AppCompatActivity() {

    private lateinit var emailtxt: EditText
    private lateinit var enviarbtn: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_password_recovery)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        iniciarVariables()

        enviarbtn.setOnClickListener { enviarCorreo() }

    }

    private fun iniciarVariables(){
        emailtxt = findViewById(R.id.email_txt)
        enviarbtn = findViewById(R.id.sendEmail_btn)
        auth = Firebase.auth
    }

    private fun validarCorreo(correo: String) : Boolean {
        var datosValidos = false
        if(correo != "" && correo.contains('@')){
            datosValidos = true
        }
        return datosValidos
    }

    fun enviarCorreo(){
        val correo = emailtxt.text.toString()
        if(validarCorreo(correo)){
            Firebase.auth.sendPasswordResetEmail(correo)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email de recuperación enviado correctamente", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}