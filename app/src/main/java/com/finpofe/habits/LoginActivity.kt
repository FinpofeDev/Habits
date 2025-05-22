package com.finpofe.habits

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.finpofe.habits.objetos.Usuario
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var emailEdt: EditText
    private lateinit var passEdt: EditText
    private lateinit var loginBtn: Button
    private lateinit var googlebtn: FrameLayout
    private lateinit var registerbtn: TextView
    private lateinit var context: Context
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        iniciarVariables()

        loginBtn.setOnClickListener {
            iniciarSesion()
        }

        googlebtn.setOnClickListener {
            iniciarCredentialManager()
        }

        registerbtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


    }

    private fun iniciarVariables(){
        auth = Firebase.auth
        database = Firebase.database.reference

        emailEdt = findViewById(R.id.email_txt)
        passEdt = findViewById(R.id.password_txt)
        loginBtn = findViewById(R.id.login_btn)
        googlebtn = findViewById(R.id.container_login)
        registerbtn = findViewById(R.id.register_lbl)
        context = this
    }

    private fun validarDatos(correo: String, pass: String): Boolean {
        var datosCorrectos = false
        if (correo != "" && correo.contains('@') && pass != "") {
            datosCorrectos = true
        }
        return datosCorrectos
    }

    private fun iniciarSesion () {
        val correo = emailEdt.text.toString()
        val pass = passEdt.text.toString()


        if (validarDatos(correo, pass)) {
            auth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener (this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(context, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(context, MainActivity::class.java))
                } else{
                    Toast.makeText(context, "Fallo al iniciar sesion", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Ingrese un correo o contraseña validos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun iniciarCredentialManager () {
       val credentialManager = CredentialManager.create(this@LoginActivity)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            mostrarCarga()
            try {
                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )
                manejarInicioSesionGoogle(result.credential)
            } catch (e: GetCredentialException) {
                Toast.makeText(context, "Error al recuperar credenciales", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Error en CredentialManager: ", e)

            } finally {
                ocultarCarga()
            }
        }

    }

    private fun iniciarSesionConGoogle (idToken: String) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credencial).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                val userId = user?.uid?: ""

                val nombreCompleto = user?.displayName
                val correo = user?.email

                val usuario = Usuario(nombreCompleto, correo, "", "")
                database.child("users").child(userId).setValue(usuario)

                Toast.makeText(this@LoginActivity, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()

            } else{
                Toast.makeText(context, "Fallo al iniciar sesion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun manejarInicioSesionGoogle (credencial: Credential) {
        if (credencial is CustomCredential && credencial.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credencial.data)
            iniciarSesionConGoogle (googleIdTokenCredential.idToken)
        } else {
            Toast.makeText(context, "La credencial no es de tipo Google ID", Toast.LENGTH_SHORT).show()
        }
    }

    @UiThread
    private fun mostrarCarga() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog?.show()
    }

    @UiThread
    private fun ocultarCarga() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

}