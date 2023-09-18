package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.fragments.FragmentReservas
import com.example.myapplication.fragments.FragmentLocation
import com.example.myapplication.fragments.FragmentPerfil
import com.example.myapplication.fragments.FragmentActividades
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView

class MainActivity : BaseOnBackPressedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Carga una fragment de inicio por defecto
        loadFragment(FragmentPerfil())

        //Recupera la barra inferior
        val bnv : NavigationBarView = findViewById(R.id.bottomNavigationView)
        //Carga un fragment u otro dependiendo del item seleccionado en la barra inferior
        bnv.setOnItemSelectedListener {
            item ->
                when(item.itemId) {
                    R.id.page_1 -> {
                        loadFragment(FragmentPerfil())
                        true
                    }
                    R.id.page_2 -> {
                        loadFragment(FragmentActividades())
                        true
                    }
                    R.id.page_3 -> {
                        loadFragment(FragmentReservas())
                        true
                    }
                    R.id.page_4 -> {
                        loadFragment(FragmentLocation())
                        true
                    }
                    else -> false
                }
        }

        //Recupera la barra superior
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        toolbar.setOnMenuItemClickListener {
            item ->
                when (item.itemId) {
                    R.id.mtb_log_out -> {
                        val sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                        //Borra el contenido del fichero
                        with (sharedPrefs.edit()){
                            clear()
                            apply()
                        }
                        val intent = Intent(this, LoginActivity()::class.java)
                        startActivity(intent)
                        finish()

                        true
                    }

                    else -> false
                }
        }
    }

    //Carga el fragment que se le pase como argumento
    private fun loadFragment (fragment: Fragment){

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        //Indica en qué elemento se incrustará qué fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment).addToBackStack(null)
        //Realiza la operación
        fragmentTransaction.commit()
    }

    //Modifica la funcionalidad del botón Atrás
    override fun onBackPressed() {
        onTwiceBackPressed(this)
    }

}


