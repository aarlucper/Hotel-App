package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.jakewharton.threetenabp.AndroidThreeTen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class LoginActivity : BaseOnBackPressedActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        AndroidThreeTen.init(this)          //Para poder trabajar con fechas en sdk inferiores al 26
        setContentView(R.layout.activity_login)

        val btnEntrar = findViewById<Button>(R.id.buttonEntrar)

        btnEntrar.setOnClickListener {
            comprobarUsuario()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun almacenarDatosUsuario(usuario: String, displayName: String?) {

        //Crea las fechas de entrada y salida del usuario (7 días por defecto)
        val currentDate = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDateStr = dateFormatter.format(currentDate)
        val salidaDateStr = dateFormatter.format(currentDate.plusDays(7))

        //Almacena los datos en un fichero shared preferences
        val sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with (sharedPrefs.edit()){
            putString(getString(R.string.login_usuario), usuario)
            putString("Usuario_nombre", displayName)
            putString("Usuario_entrada", currentDateStr)
            putString("Usuario_salida", salidaDateStr)
            apply()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun autenticarUsuario(usuario: String, passw: String) {

        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(usuario, passw)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val displayName = user?.displayName
                    //crearNombreUsuario()
                    almacenarDatosUsuario(usuario, displayName)

                    //Pasa a MainActivity
                    val haciaMainActivity = Intent(this, MainActivity::class.java)
                    startActivity(haciaMainActivity)
                }
                else {
                    Toast.makeText(this@LoginActivity, "Login incorrecto", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun comprobarUsuario(){

        //Recupera el texto introducido en el campo de texto
        val tilUsuario = findViewById<TextInputLayout>(R.id.tilNombre)
        val titeUsuario = tilUsuario.findViewById<TextInputEditText>(R.id.titeUsuario)
        val usuario = titeUsuario.text.toString()

        //Recupera el texto introducido en el campo de texto
        val tilPassw = findViewById<TextInputLayout>(R.id.tilPassw)
        val titePassw = tilPassw.findViewById<TextInputEditText>(R.id.titePassw)
        val passw = titePassw.text.toString()

        //Pasa a la pantalla del administrador si el usuario se identifica como tal
        if (usuario.equals("admin") && passw.equals("admin")){
            val haciaAdminActivity = Intent(this, AdminActivity::class.java)
            startActivity(haciaAdminActivity)
        }
        else{
            autenticarUsuario(usuario, passw)
        }
    }

    //Le da un nombre de usuario al usuario actual, simulando que se ha añadido al registrarse
    fun crearNombreUsuario(user: FirebaseUser?) {

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("Albertito")        //Nombre del usuario
            .build()

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener(){ task ->
                if (task.isSuccessful){
                    Toast.makeText(this@LoginActivity, "Yupi", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@LoginActivity, "Oh no", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //Modifica la funcionalidad del botón Atrás
    override fun onBackPressed() {
        onTwiceBackPressed(this)
    }
}