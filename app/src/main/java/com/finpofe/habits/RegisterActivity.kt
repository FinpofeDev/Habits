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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
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
        val datePickerDialog = DatePickerDialog(this, { DatePicker, year: Int, mes: Int, dia: Int ->
            val fechaSeleccionada = Calendar.getInstance()
            fechaSeleccionada.set(year, mes, dia)
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaFormateada = formatoFecha.format(fechaSeleccionada.time)
            fechalbl.text = fechaFormateada
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
            )

        datePickerDialog.show()

    }

    private fun registrarUsuario() {
        nombreCompleto = nombretxt.text.toString()
        correo = correotxt.text.toString()
        pass = passtxt.text.toString()
        rePass = repasstxt.text.toString()
        fechaNacimiento = fechalbl.text.toString()

        var mensaje = "Se ha producido un error"
        var datosValidos = true

        if(!correo.contains('@')) {
            mensaje = "Ingrese un correo valido"
            datosValidos = false
        }
        if(pass != rePass) {
            mensaje = "Las contraseñas no coinciden"
            datosValidos = false
        }
        if(pass.length < 6){
            mensaje = "Las contraseñas debe tener al menos 6 caracteres"
            datosValidos = false
        }
        if(fechaNacimiento == R.string.fecha_default.toString()) {
            mensaje = "Seleccione una fecha de nacimiento"
            datosValidos = false
        }

        if(bitmapImgPerfil == null) {
            mensaje = "No selecciono foto de perfil"
            datosValidos = false
        }else {
            nombreFotoPerfil = ImageController.guardarImagen(this@RegisterActivity, bitmapImgPerfil)
        }

        if(datosValidos) {
            lifecycleScope.launch {
                mostrarCarga()
                delay(2000)
                auth.createUserWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(this@RegisterActivity) { task ->
                        if (task.isSuccessful) {
                            val user: FirebaseUser? = auth.currentUser
                            val userId = user?.uid.toString()
                            val usuario = Usuario(nombreCompleto, correo, fechaNacimiento, nombreFotoPerfil)
                            database.child("users").child(userId).setValue(usuario)
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        } else {
                            Toast.makeText(this@RegisterActivity, "Ocurrio un error.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ocultarCarga()
            }
        }else{
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

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

data class Usuario (val nombre: String? = null, val correo: String? = null, val fechaNacimiento: String? = null, val pfpNombre: String? = null)