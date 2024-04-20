package com.src.localite

import androidx.recyclerview.widget.DiffUtil

class DestinoDiffCallback(
    private val oldList: List<Destino>,
    private val newList: List<Destino>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].nombre == newList[newItemPosition].nombre
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
