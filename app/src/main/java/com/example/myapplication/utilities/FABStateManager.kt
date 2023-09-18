package com.example.myapplication.utilities

import android.content.Context
import android.content.SharedPreferences

class FABStateManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("FABState", Context.MODE_PRIVATE)

    //Guarda el estado del botón
    fun saveFABState (itemId: String, isEnabled: Boolean){
        sharedPreferences.edit().putBoolean(itemId, isEnabled).apply()
    }

    //Recupera el estado del botón
    fun getFABState (itemId: String): Boolean {
        return sharedPreferences.getBoolean(itemId, true) //Devuelve true por defecto
    }
}