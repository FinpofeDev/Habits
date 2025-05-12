package com.finpofe.habits

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEdt: EditText
    private lateinit var passEdt: EditText
    private lateinit var loginBtn: Button
    private lateinit var googlebtn: ImageView
    private lateinit var registerbtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        emailEdt = findViewById(R.id.email_txt)
        passEdt = findViewById(R.id.password_txt)
        loginBtn = findViewById(R.id.login_Btn)
        googlebtn = findViewById(R.id.google_img)
        registerbtn = findViewById(R.id.register_lbl)

        loginBtn.setOnClickListener {
            val emailString = emailEdt.text.toString()
            val passString = passEdt.text.toString()

            auth.signInWithEmailAndPassword(emailString, passString).addOnCompleteListener (this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(baseContext, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                } else{
                    Toast.makeText(baseContext, "Fallo al iniciar sesion", Toast.LENGTH_SHORT).show()
                }
            }
        }

        googlebtn.setOnClickListener {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(getString(R.string.id_client_web))
                .setFilterByAuthorizedAccounts(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
        }

        registerbtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }
}