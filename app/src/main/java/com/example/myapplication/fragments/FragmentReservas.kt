package com.example.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.Item
import com.example.myapplication.adapter.ItemAdapter
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
 * Use the [FragmentReservas.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentReservas : Fragment(), ItemAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var rv: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var reservas: CollectionReference
    private lateinit var list: ArrayList<Item>

    override fun onItemClick(item: Item, fab: FloatingActionButton) {

        //Realiza una consulta con los pares clave valor correspondientes al item
        val documento = reservas
            .whereEqualTo("desc", item.desc)
            .whereEqualTo("titulo", item.titulo)

        //Obtiene el documento y lo elimina de la base de datos
        documento.get()
            .addOnSuccessListener { result ->
                for (document in result){
                    document.reference.delete()
                        .addOnSuccessListener { Toast.makeText(context, "Reserva cancelada", Toast.LENGTH_SHORT).show() }
                        .addOnFailureListener { Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_SHORT).show() }
                }
                // Elimina el item de la lista y notifica al adaptador
                val deletedItemIndex = list.indexOf(item)
                list.removeAt(deletedItemIndex)
                rv.adapter?.notifyItemRemoved(deletedItemIndex)

                //Reactiva el botón
                val fabStateManager = FABStateManager(fab.context)
                fabStateManager.saveFABState(item.id, true)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al leer el fichero", Toast.LENGTH_SHORT).show()
            }
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

        list = ArrayList()
        db = Firebase.firestore
        reservas = db.collection("reservas")

        reservas.get()
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

                rv.adapter = ItemAdapter(list, this, true)
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
         * @return A new instance of fragment FragmentActividades.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentReservas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}