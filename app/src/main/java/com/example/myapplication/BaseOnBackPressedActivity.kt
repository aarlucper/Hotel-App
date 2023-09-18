package com.example.myapplication

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class BaseOnBackPressedActivity : AppCompatActivity() {

    private val timeLimit = 2000
    private var onBackPressedTimestamp : Long = 0
    private var onTwiceBackPressedTimestamp : Long = 0

    //Controla la doble pulsación del botón Atrás dado un contexto concreto
    open fun onTwiceBackPressed(context: Context){
        //Registra una pulsación
        onTwiceBackPressedTimestamp = System.currentTimeMillis()

        if (onTwiceBackPressedTimestamp - onBackPressedTimestamp <= timeLimit)
            //La diferencia entre ambas pulsaciones está dentro del límite
            finishAffinity()    //Cierra la aplicación
        else
            //La diferencia entre ambas pulsaciones está fuera del límite
            Toast.makeText(context, "Pulsa de nuevo para salir", Toast.LENGTH_SHORT).show()

        //Actualiza la timestamp de la primera pulsación a la de la última
        onBackPressedTimestamp = onTwiceBackPressedTimestamp

    }
}