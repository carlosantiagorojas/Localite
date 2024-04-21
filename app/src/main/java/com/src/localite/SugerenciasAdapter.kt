package com.src.localite

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class SugerenciasAdapter(private val sugerencias: List<String>) : RecyclerView.Adapter<SugerenciasAdapter.ViewHolder>() {

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val textView: TextView = cardView.findViewById(R.id.sugerencia_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sugerencia_item, parent, false) as CardView
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = sugerencias[position]
    }

    override fun getItemCount() = sugerencias.size
}