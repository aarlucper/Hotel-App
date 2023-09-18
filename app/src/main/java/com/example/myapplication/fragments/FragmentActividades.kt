package com.example.myapplication.fragments

import com.example.myapplication.adapter.Item
import com.example.myapplication.adapter.ItemAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.utilities.FABStateManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentActividades.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentActividades : Fragment(), ItemAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var rv: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var actividades: CollectionReference

    override fun onItemClick(item: Item, fab: FloatingActionButton) {

        //Referencia la colección a la que voy a copiar el documento
        val reservas = db.collection("reservas")
        //Crea un hashmap con los datos que contendrá el documento
        val docData = hashMapOf(
            "id" to item.id,
            "titulo" to item.titulo,
            "desc" to item.desc,
            "src" to item.src,
            "latitude" to item.latitude,
            "longitude" to item.longitude
        )
        //Añade el documento
        reservas.add(docData)
            .addOnSuccessListener { Toast.makeText(context, "Reserva realizada", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(context, "Algo ha ido mal", Toast.LENGTH_SHORT).show() }

        //Desactiva el botón y guarda su estado
        val fabStateManager = FABStateManager(fab.context)
        fabStateManager.saveFABState(item.id, false)
        fab.isEnabled = false

    }

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
        val view = inflater.inflate(R.layout.fragment_actividades, container, false)
        rv = view.findViewById<RecyclerView>(R.id.myRecyclerView)

        var list: ArrayList<Item> = ArrayList()
        db = Firebase.firestore
        actividades = db.collection("actividades")

        actividades.get()
            .addOnSuccessListener { result ->
                for(document in result){
                    var item = Item(document.get("id").toString(),
                                    document.get("titulo").toString(),
                                    document.get("desc").toString(),
                                    document.get("src").toString(),
                                    document.getDouble("latitude")!!,
                                    document.getDouble("longitude")!!
                    )
                    list.add(item)
                }

                rv.adapter = ItemAdapter(list, this, false)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al leer el fichero", Toast.LENGTH_SHORT).show()
            }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentReservar.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentActividades().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}