package com.src.localite

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class DestinoAdapter(val destinations: MutableList<Destino>) : RecyclerView.Adapter<DestinoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.titulo)
        val tag: TextView = view.findViewById(R.id.tag)
        val distancia: TextView = view.findViewById(R.id.distancia)
        val descripcion: TextView = view.findViewById(R.id.descripcion)
        val imagenDestino: ImageView = view.findViewById(R.id.imagenDestino)
        val btnDescubrir: Button = view.findViewById(R.id.btnDescubrir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destino = destinations[position]
        holder.titulo.text = destino.nombre ?: "Nombre no disponible"
        holder.tag.text = destino.Tags.filterNotNull().joinToString(separator = ", ")
        holder.descripcion.text = destino.Descripcion ?: "Descripción no disponible"
        holder.distancia.text = holder.itemView.context.getString(R.string.distance_km, destino.calculatedDistance)

        val storageReference = FirebaseStorage.getInstance().reference.child("Lugares/${destino.nombre}/Principal.jpg")
        storageReference.downloadUrl.addOnSuccessListener { uri: Uri ->
            Glide.with(holder.itemView.context).load(uri).into(holder.imagenDestino)
        }.addOnFailureListener {
            holder.imagenDestino.setImageResource(R.drawable.error_image) // Ensure this drawable resource exists
        }
        holder.btnDescubrir.setOnClickListener {
            val intent = Intent(holder.itemView.context, DestinoActivity::class.java)
            intent.putExtra("destino", destino)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = destinations.size

    fun updateDestinations(newDestinations: List<Destino>) {
        if (newDestinations != destinations) { // Ensure new data is actually new
            Log.d("RecyclerViewAdapter", "Updating destinations: ${newDestinations.size}")
            val diffCallback = DestinoDiffCallback(destinations, newDestinations)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            destinations.clear()
            destinations.addAll(newDestinations)
            diffResult.dispatchUpdatesTo(this)
        } else {
            Log.d("RecyclerViewAdapter", "No change in destinations, no update dispatched.")
        }
    }

}
