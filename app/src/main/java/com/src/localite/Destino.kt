package com.src.localite

import android.os.Parcel
import android.os.Parcelable

data class Destino(
    var nombre: String? = null,
    var Actividades: String = "",
    var Ciudad: String = "",
    var Descripcion: String = "",
    var Direccion: String = "",
    var General: String = "",
    var Historia: String = "",
    var Latitud: Double? = null,
    var Longitud: Double? = null,
    var Pais: String = "",
    var Tags: List<String?> = listOf(),
    var calculatedDistance: Double = 0.0  // Added property for distance calculation
) : Parcelable {

    constructor(parcel: Parcel) : this(
        nombre = parcel.readString(),
        Actividades = parcel.readString() ?: "",
        Ciudad = parcel.readString() ?: "",
        Descripcion = parcel.readString() ?: "",
        Direccion = parcel.readString() ?: "",
        General = parcel.readString() ?: "",
        Historia = parcel.readString() ?: "",
        Latitud = parcel.readValue(Double::class.java.classLoader) as? Double,
        Longitud = parcel.readValue(Double::class.java.classLoader) as? Double,
        Pais = parcel.readString() ?: "",
        Tags = mutableListOf<String?>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        calculatedDistance = parcel.readDouble() // Read the distance value
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeString(Actividades)
        parcel.writeString(Ciudad)
        parcel.writeString(Descripcion)
        parcel.writeString(Direccion)
        parcel.writeString(General)
        parcel.writeString(Historia)
        parcel.writeValue(Latitud)
        parcel.writeValue(Longitud)
        parcel.writeString(Pais)
        parcel.writeList(Tags)
        parcel.writeDouble(calculatedDistance)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Destino> {
        override fun createFromParcel(parcel: Parcel): Destino = Destino(parcel)
        override fun newArray(size: Int): Array<Destino?> = arrayOfNulls(size)
    }
}
