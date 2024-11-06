package com.example.taller3_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.ActivityMapaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapaBinding
    private lateinit var map: GoogleMap
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var usuarioLat: Double = 0.0
    private var usuarioLng: Double = 0.0
    private var seleccionadoLat: Double = 0.0
    private var seleccionadoLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val mailUser = intent.getStringExtra("mail")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        retrieveAuthenticatedUserLocation()
        retrieveSelectedUserLocation(mailUser)

        binding.back.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun retrieveAuthenticatedUserLocation() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.getReference("users").child(userId)

        userRef.child("latitude").get().addOnSuccessListener { latSnapshot ->
            userRef.child("longitude").get().addOnSuccessListener { lngSnapshot ->
                usuarioLat = latSnapshot.getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                usuarioLng = lngSnapshot.getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                addAuthenticatedUserMarker()
            }
        }
    }

    private fun retrieveSelectedUserLocation(email: String?) {
        if (email.isNullOrEmpty()) return

        database.getReference("users").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        seleccionadoLat = userSnapshot.child("latitude").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                        seleccionadoLng = userSnapshot.child("longitude").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                        addSelectedUserMarker()
                        break
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MapaActivity, "Error retrieving selected user location", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun addAuthenticatedUserMarker() {
        val usuarioPosition = LatLng(usuarioLat, usuarioLng)
        map.addMarker(
            MarkerOptions()
                .position(usuarioPosition)
                .title("Tú (Usuario autenticado)")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(usuarioPosition, 7f))
    }

    private fun addSelectedUserMarker() {
        val seleccionadoPosition = LatLng(seleccionadoLat, seleccionadoLng)
        map.addMarker(
            MarkerOptions()
                .position(seleccionadoPosition)
                .title("Usuario seleccionado")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
    }
}
