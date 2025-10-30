package com.finpofe.habits

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.finpofe.habits.objetos.Usuario
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var usuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        iniciarVariables()

    }

    private fun iniciarVariables() {
        auth = Firebase.auth
        database = Firebase.database.reference

        val user = Firebase.auth.currentUser

        val userId = user?.uid
        val nombre = user?.displayName
        val correo = user?.email
        var fechaNacimiento: String? = null

        if(userId != null) {
            database.child("users").child(userId).child("fechaNacimiento").get().addOnSuccessListener {
                fechaNacimiento = it.value.toString()
            }
        }

        usuario = Usuario(nombre, correo, fechaNacimiento, "")
    }

    private fun cerrarSesion(){
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_principal -> findNavController(R.id.navHostFragment).navigate(R.id.mainFragment)
            R.id.nav_habitos -> findNavController(R.id.navHostFragment).navigate(R.id.habitosFragment)
            R.id.nav_logros -> findNavController(R.id.navHostFragment).navigate(R.id.logrosFragment)
            R.id.nav_logout -> cerrarSesion()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}