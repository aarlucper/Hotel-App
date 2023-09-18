package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPerfil.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPerfil : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var tilNombre : TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        /* Toma el nombre del usuario de la pantalla de login */
        //Recupera el valor asociado a la clave del fichero shared preferences
        val sharedPrefs = view.context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val usuario = sharedPrefs.getString("Usuario_nombre", "No encontrado")
        //Escribe en el campo de texto
        tilNombre = view.findViewById<TextInputLayout>(R.id.tilNombre)
        tilNombre.editText?.setText(usuario)

        /* Toma las fechas de entrada y salida creadas al hacer login */
        val fecEntrada = sharedPrefs.getString("Usuario_entrada", "No encontrado")
        val fecSalida = sharedPrefs.getString("Usuario_salida", "No encontrado")

        val tilEntrada = view.findViewById<TextInputLayout>(R.id.tilEntrada)
        tilEntrada.editText?.setText(fecEntrada)
        val tilSalida = view.findViewById<TextInputLayout>(R.id.tilSalida)
        tilSalida.editText?.setText(fecSalida)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentPerfil.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentPerfil().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}