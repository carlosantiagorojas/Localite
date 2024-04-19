package com.src.localite

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.src.localite.databinding.ActivityFiltroDistanciaBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FiltroDistanciaActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityFiltroDistanciaBinding
    private lateinit var locationManager: LocationManager
    private lateinit var adapter: DestinoAdapter
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltroDistanciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DestinoAdapter(mutableListOf())
        binding.recyclerViewFiltroDistancia.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFiltroDistancia.adapter = adapter

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        checkLocationPermission()
        loadDataFromFirebase()

        // Initialize the slider with snap functionality
        val steps = floatArrayOf(1f, 5f, 10f, 25f, 50f, 75f, 100f)
        binding.sliderFiltroDistancia.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val nearestValue = steps.minByOrNull { kotlin.math.abs(value - it) } ?: value
                slider.value = nearestValue
                filterDestinations(nearestValue)
            }
        }
    }

    private fun loadDataFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Lugares")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newDestinations = snapshot.children.mapNotNull { childSnapshot ->
                    childSnapshot.getValue(Destino::class.java)?.copy(nombre = childSnapshot.key)
                }.toMutableList()
                adapter.updateDestinations(newDestinations)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        filterDestinations(binding.sliderFiltroDistancia.value)
    }

    // Method to filter destinations based on the slider's value
    private fun filterDestinations(maxDistance: Float) {
        if (currentLocation == null) return

        val filteredDestinations = adapter.destinations.filter { destino ->
            val distance = calculateDistance(currentLocation!!, destino) / 1000 // Convert to kilometers
            distance <= maxDistance
        }
        adapter.updateDestinations(filteredDestinations)
    }

    private fun calculateDistance(currentLocation: Location, destino: Destino): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            destino.Latitud ?: 0.0,
            destino.Longitud ?: 0.0,
            results
        )
        return results[0] // distance in meters
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }
}