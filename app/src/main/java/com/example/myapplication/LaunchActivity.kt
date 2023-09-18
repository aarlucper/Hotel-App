package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.utilities.FABStateManager

class LaunchActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()    //Muestra la SplashScreen en versiones de Android inferiores a 12

        super.onCreate(savedInstanceState)

        //Abre el fichero shared preferences y comprueba si está vacío
        val sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        //Recupera las entradas del archivo shared preferences
        val preferences : Map<String, *> = sharedPrefs.all


        if (preferences.isEmpty()){    //Comprueba si hay una sesión abierta
            //Muestra la pantalla de login
            setContentView(R.layout.activity_login)
            val intent = Intent(this, LoginActivity()::class.java)
            startActivity(intent)
        }
        else{
            //Salta la pantalla de login
            setContentView(R.layout.activity_main)
            val intent = Intent(this, MainActivity()::class.java)
            startActivity(intent)
        }
    }
}