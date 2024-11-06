package com.example.taller3_firebase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.ActivityMapaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
        usuarioLat = intent.getDoubleExtra("usuario_lat", 0.0)
        usuarioLng = intent.getDoubleExtra("usuario_lng", 0.0)
        seleccionadoLat = intent.getDoubleExtra("seleccionado_lat", 0.0)
        seleccionadoLng = intent.getDoubleExtra("seleccionado_lng", 0.0)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val usuarioPosition = LatLng(usuarioLat, usuarioLng)
        map.addMarker(
            MarkerOptions()
                .position(usuarioPosition)
                .title("Tú (Usuario autenticado)")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )

        val seleccionadoPosition = LatLng(seleccionadoLat, seleccionadoLng)
        map.addMarker(
            MarkerOptions()
                .position(seleccionadoPosition)
                .title("Usuario seleccionado")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(usuarioPosition, 10f))
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}
