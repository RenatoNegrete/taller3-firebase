package com.example.taller3_firebase


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.taller3_firebase.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
        binding.btnModificarDatos.setOnClickListener {
            modificarDatos()
        }
        binding.btnCambiarDisponibilidad.setOnClickListener {
            cambiarDisponibilidad()
        }
        binding.btnlista.setOnClickListener {
            val intent: Intent = Intent(this, ListaUsuarioActivity::class.java)
            startActivity(intent)

        }

    }

    private fun cerrarSesion() {
        auth.signOut()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun modificarDatos() {
        val intent = Intent(this, ModificarActivity::class.java)
        startActivity(intent)
    }

    private fun cambiarDisponibilidad() {
        val userId = auth.currentUser?.uid ?: return

        val userRef = database.getReference("users").child(userId)

        userRef.child("disp").get().addOnSuccessListener { snapshot ->
            val disponible = snapshot.value as? Boolean ?: false
            userRef.child("disp").setValue(!disponible)
                .addOnSuccessListener {
                    Toast.makeText(this, "Disponibilidad actualizada a ${!disponible}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cambiar disponibilidad", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
