package com.src.localite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.src.localite.databinding.ActivityFiltroDistanciaBinding

class FiltroDistanciaActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityFiltroDistanciaBinding
    private lateinit var locationManager: LocationManager
    private lateinit var adapter: DestinoAdapter
    private var currentLocation: Location? = null
    private var allDestinations = mutableListOf<Destino>()
    private var isLocationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltroDistanciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DestinoAdapter(mutableListOf())
        binding.recyclerViewFiltroDistancia.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFiltroDistancia.adapter = adapter

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        checkLocationPermission()

        val steps = floatArrayOf(1f, 5f, 10f, 25f, 50f, 75f, 100f)
        binding.sliderFiltroDistancia.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val nearestValue = steps.minByOrNull { kotlin.math.abs(value - it) } ?: value
                slider.value = nearestValue
                filterDestinations(nearestValue)
            }
        }

        binding.homeIconFiltroDistancia.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.accountIconFiltroDistancia.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        setupFirebaseListener()
    }

    private fun setupFirebaseListener() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Lugares")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseData", "Snapshot received: ${snapshot.childrenCount} children")
                allDestinations.clear()
                allDestinations.addAll(snapshot.children.mapNotNull { it.getValue(Destino::class.java)?.apply { nombre = it.key } })
                if (isLocationPermissionGranted && currentLocation != null) {
                    filterDestinations(binding.sliderFiltroDistancia.value)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun filterDestinations(maxDistance: Float) {
        if (currentLocation == null) return

        val filteredDestinations = allDestinations.mapNotNull { destino ->
            val distanceInKm = calculateDistance(currentLocation!!, destino) / 1000
            if (distanceInKm <= maxDistance) {
                destino.apply { calculatedDistance = distanceInKm.toDouble() }
            } else {
                null
            }
        }

        if (filteredDestinations.isEmpty()) {
            binding.noDestinationsText.visibility = View.VISIBLE
        } else {
            binding.noDestinationsText.visibility = View.GONE
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
        return results[0]
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        } else {
            isLocationPermissionGranted = true
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true
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
