package com.src.localite

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.src.localite.databinding.ActivityFiltroCategoriaEspecificaBinding
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class FiltroCategoriaEspecificaActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityFiltroCategoriaEspecificaBinding
    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private var informacionCargada = false
    private var tagActual: String? = null
    private val RADIUS_OF_EARTH_KM = 6371

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltroCategoriaEspecificaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tagActual =  intent.getStringExtra("Categoria")
        println("Tag actual $tagActual")
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        setupButtons()
        checkLocationPermission()


    }
    override fun onResume() {
        super.onResume()

        if(checkLocationPermission()){
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                currentLocation = lastKnownLocation
                println("Última ubicación conocida: ${lastKnownLocation.latitude}, ${lastKnownLocation.longitude}")
            } else {
                println("No se encontró última ubicación conocida")
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, this)

        }

    }
    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), Datos.MY_PERMISSION_REQUEST_LOCATION)
            if(!informacionCargada){
                loadDataFromFirebase()
                informacionCargada = true
            }
            return false
        }
        if(!informacionCargada){
            loadDataFromFirebase()
            informacionCargada = true
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Datos.MY_PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLocationAndLoadData()  // Carga datos y establece ubicación inicial
            } else {
                println("Permiso de ubicación denegado")
                if(!informacionCargada){
                    loadDataFromFirebase()
                    informacionCargada = true
                }
            }
        }
    }

    private fun initializeLocationAndLoadData() {
        if (checkLocationPermission()) {
            val lastKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                currentLocation = lastKnownLocation
                println("Última ubicación conocida: ${lastKnownLocation.latitude}, ${lastKnownLocation.longitude}")
            } else {
                println("No se encontró última ubicación conocida")
            }
            if (!informacionCargada) {
                loadDataFromFirebase()
                informacionCargada = true
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, this)
        }
    }



    private fun updateDistances() {
        val childCount = binding.container.childCount
        for (i in 0 until childCount) {
            val cardView = binding.container.getChildAt(i) as? ViewGroup
            val destino = cardView?.tag as? Destino
            if (destino != null) {
                val distanciaTextView = cardView.findViewById<TextView>(R.id.distancia)
                calcularDistancia(destino, distanciaTextView)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }


    private fun addCardToLayout(destino: Destino) {
        val cardView = layoutInflater.inflate(R.layout.layout_card, binding.container, false)
        val titulo = cardView.findViewById<TextView>(R.id.titulo)
        val tag = cardView.findViewById<TextView>(R.id.tag)
        val direccion = cardView.findViewById<TextView>(R.id.distancia)  // Corrige aquí, estaba mal referenciado
        val descripcion = cardView.findViewById<TextView>(R.id.descripcion)
        val imagenDestino = cardView.findViewById<ImageView>(R.id.imagenDestino)
        val btnDescubrir = cardView.findViewById<Button>(R.id.btnDescubrir)
        println("Localizacion en destino ${destino.Latitud} y ${destino.Longitud}")
        calcularDistancia(destino, direccion)



        titulo.text = destino.nombre ?: "Nombre no disponible"
        tag.text = destino.Tags.filterNotNull().joinToString(separator = ", ")

        descripcion.text = destino.Descripcion ?: "Descripción no disponible"

        // Utilizar Firebase Storage SDK para obtener la URL de la imagen
        val storageReference = FirebaseStorage.getInstance().reference.child("Lugares/${destino.nombre}/Principal.jpg")
        storageReference.downloadUrl.addOnSuccessListener { uri: Uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.load_image)
                .error(R.drawable.error_image)
                .into(imagenDestino)
            println("Imagen cargada con éxito desde: $uri")
        }.addOnFailureListener { exception ->
            println("Error al cargar la imagen: ${exception.message}")
            imagenDestino.setImageResource(R.drawable.error_image)
        }

        btnDescubrir.setOnClickListener {
            val intent = Intent(this, DestinoActivity::class.java).apply {
                putExtra("destino", destino) // "destino" es la clave para acceder al objeto en la actividad de destino
            }
            startActivity(intent)
        }
        binding.container.addView(cardView)
    }

    private fun setupButtons() {

        binding.includeBottomBar.accountIcon.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        binding.includeBottomBar.homeIcon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.includeTopBar.Logo.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadDataFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Lugares")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var encontrados = false // Variable para rastrear si se encontró algún destino correspondiente
                for (lugarSnapshot in dataSnapshot.children) {
                    val destino = lugarSnapshot.getValue<Destino>()?.copy(nombre = lugarSnapshot.key)
                    val tagsSinNull = destino?.Tags?.filterNotNull() ?: emptyList()
                    if (destino != null && tagsSinNull.contains(tagActual)) {
                        encontrados = true // Se encontró al menos un destino correspondiente
                        addCardToLayout(destino)
                    } else {
                        println("El destino no corresponde a la categoría: ${lugarSnapshot.key}")
                    }
                }
                if (!encontrados) {
                    binding.mensajeErrorTags.visibility = View.VISIBLE
                } else {
                    binding.mensajeErrorTags.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error cargando datos: ${databaseError.message}")
            }
        })
    }


    private fun calcularDistancia(destino: Destino, textView: TextView) {
        val currentLat = currentLocation?.latitude
        val currentLng = currentLocation?.longitude
        val destinoLat = destino.Latitud
        val destinoLng = destino.Longitud

        println("Entre a calcular distancia")


        if (currentLat == null || currentLng == null || destinoLat == null || destinoLng == null) {
            textView.text = "Ubicación no disponible"
        } else {
            val latDistance = Math.toRadians(destinoLat - currentLat)
            val lngDistance = Math.toRadians(destinoLng - currentLng)
            val a = (sin(latDistance / 2) * sin(latDistance / 2) +
                    cos(Math.toRadians(destinoLat)) * cos(Math.toRadians(currentLat)) *
                    sin(lngDistance / 2) * sin(lngDistance / 2))
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val result = RADIUS_OF_EARTH_KM * c
            val distance = (result * 100.0).roundToInt() / 100.0

            textView.text = "Estás a $distance km"
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        updateDistances()  // Actualiza distancias en las tarjetas    }
    }

}