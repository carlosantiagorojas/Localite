package com.src.localite

import android.Manifest
import android.content.Intent
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
    }

    private fun loadDataFromFirebase() {
        val ref = FirebaseDatabase.getInstance().getReference("Lugares")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newDestinations = mutableListOf<Destino>()
                snapshot.children.mapNotNullTo(newDestinations) {
                    it.getValue(Destino::class.java)?.copy(nombre = it.key)
                }
                adapter.updateDestinations(newDestinations)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
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
        // Here you would typically notify the adapter to update distances based on the new location
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
