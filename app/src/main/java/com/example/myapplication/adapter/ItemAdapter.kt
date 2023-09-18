package com.example.myapplication.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.fragments.FragmentReservas
import com.example.myapplication.utilities.FABStateManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ItemAdapter(
    private val items: List<Item>,
    private val listener: OnItemClickListener,
    private val onFragmentReservas : Boolean
    ) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    //Interfaz de callback
    interface OnItemClickListener{
        fun onItemClick(item: Item, fab: FloatingActionButton)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val titulo = view.findViewById<TextView>(R.id.textView_titulo)
        private val desc = view.findViewById<TextView>(R.id.textView_desc)
        private val img = view.findViewById<ImageView>(R.id.ivActividad)

        fun bind (item: Item){
            titulo.text = item.titulo
            desc.text = item.desc

            Glide.with(itemView.context)
                .load(item.src)
                .into(img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        //Botón de añadir actividad
        val btnAdd: FloatingActionButton = holder.itemView.findViewById(R.id.fabAdd)
        //Recupera el estado del botón
        val fabStateManager = FABStateManager(btnAdd.context)
        val isFABEnabled = fabStateManager.getFABState(item.id)
        //Persiste el estado de los botones en FragmentActividades
        if (!onFragmentReservas)
            btnAdd.isEnabled = isFABEnabled
        else {
            btnAdd.setImageResource(R.drawable.ic_baseline_remove_24)
        }

        btnAdd.setOnClickListener {
            listener.onItemClick(item, btnAdd)
        }



        //Botón de compartir actividad
        val btnShare : FloatingActionButton = holder.itemView.findViewById(R.id.fabShare)
        btnShare.setOnClickListener{

        //Acciones para compartir la actividad
        val shareIntent = Intent()

        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Apúntate a la actividad: " +
                item.titulo + "\n" + item.desc)

        startActivity(it.context, Intent.createChooser(shareIntent, null), null)

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}