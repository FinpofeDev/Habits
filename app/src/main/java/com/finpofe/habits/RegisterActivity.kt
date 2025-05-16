package com.finpofe.habits

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.finpofe.habits.objetos.ImageController
import com.finpofe.habits.objetos.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var nombretxt: EditText
    private lateinit var correotxt: EditText
    private lateinit var passtxt: EditText
    private lateinit var repasstxt: EditText
    private lateinit var fechalbl: TextView
    private lateinit var selecFechaBtn: Button
    private lateinit var selecImgProf: Button
    private lateinit var imgProf: ImageView
    private lateinit var registerBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var nombreCompleto = ""
    private var correo = ""
    private var pass = ""
    private var rePass = ""
    private var fechaNacimiento = ""
    private var fotoPerfil: Uri? = null
    private var nombreFotoPerfil = ""

    private var loadingDialog: LoadingDialog? = null

    private var bitmapImgPerfil: Bitmap? = null

    private val calendar = Calendar.getInstance()

    private val SELECT_ACTIVITY = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarVariables()

        selecFechaBtn.setOnClickListener {
            mostrarDatePicker()
        }

        selecImgProf.setOnClickListener {
            ImageController.seleccionarFotoDesdeGaleria(this, SELECT_ACTIVITY)
            ImageController.guardarImagen(this, bitmapImgPerfil)
        }

        registerBtn.setOnClickListener {
            registrarUsuario()
        }

    }

    private fun inicializarVariables(){
        nombretxt = findViewById(R.id.nombre_txt)
        correotxt = findViewById(R.id.correo_txt)
        passtxt = findViewById(R.id.pass_txt)
        repasstxt = findViewById(R.id.repass_txt)
        fechalbl = findViewById(R.id.fecha_lbl)
        selecFechaBtn = findViewById(R.id.fecha_btn)
        selecImgProf = findViewById(R.id.imagen_btn)
        imgProf = findViewById(R.id.img_prof)
        registerBtn = findViewById(R.id.register_btn)
        auth = Firebase.auth
        database = Firebase.database.reference
    }

    private fun mostrarDatePicker() {
        val datePickerDialog = DatePickerDialog(this, { datePicker, year: Int, mes: Int, dia: Int ->
            val fechaSeleccionada = Calendar.getInstance()
            fechaSeleccionada.set(year, mes, dia)
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaFormateada = formatoFecha.format(fechaSeleccionada.time)
            fechalbl.text = fechaFormateada
            fechaNacimiento = fechalbl.text.toString()
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
            )

        datePickerDialog.show()

    }

    private fun registrarUsuario() {
        if(validarDatos()) {
            lifecycleScope.launch {
                mostrarCarga()
                delay(2000)
                auth.createUserWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(this@RegisterActivity) { task ->
                        if (task.isSuccessful) {
                            val user: FirebaseUser? = auth.currentUser
                            val userId = user?.uid?: ""

                            val usuario = Usuario(nombreCompleto, correo, fechaNacimiento, nombreFotoPerfil)
                            database.child("users").child(userId).setValue(usuario)

                            val profileUpdates = userProfileChangeRequest {
                                displayName = nombreCompleto
                            }

                            user?.updateProfile(profileUpdates)?.addOnCompleteListener { task2 ->
                                if(task2.isSuccessful){
                                    user.reload().addOnSuccessListener {
                                        Toast.makeText(this@RegisterActivity, "Usuario registrado con exito.", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this@RegisterActivity, "Ocurrio un error.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ocultarCarga()
            }
        }
    }

    private fun validarDatos(): Boolean {
        nombreCompleto = nombretxt.text.toString()
        correo = correotxt.text.toString()
        pass = passtxt.text.toString()
        rePass = repasstxt.text.toString()

        var datosValidos = true

        if(correo.isBlank() && nombreCompleto.isBlank() && pass.isBlank() && rePass.isBlank()){
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show()
            datosValidos = false
        }
        if(!correo.contains('@')) {
            Toast.makeText(this, "Ingrese un correo valido", Toast.LENGTH_SHORT).show()
            datosValidos = false
        }
        if(pass != rePass) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            datosValidos = false
        }
        if(pass.length < 6){
            Toast.makeText(this, "Las contraseñas debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            datosValidos = false
        }
        if(fechaNacimiento.isBlank()) {
            Toast.makeText(this, "Seleccione una fecha de nacimiento", Toast.LENGTH_SHORT).show()
            datosValidos = false
        }

        return datosValidos
    }

    @Deprecated("Utiliza una API antigua")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when{
            requestCode == SELECT_ACTIVITY && resultCode == Activity.RESULT_OK -> {
                fotoPerfil = data!!.data
                try {
                    fotoPerfil?.let { uri ->
                        val inputStream = contentResolver.openInputStream(uri)
                        bitmapImgPerfil = BitmapFactory.decodeStream(inputStream)
                        imgProf.setImageBitmap(bitmapImgPerfil)
                        inputStream?.close()

                    }
                }catch (e: IOException) {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
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
