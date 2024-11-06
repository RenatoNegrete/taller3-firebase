package com.example.taller3_firebase

import UsuarioAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taller3_firebase.HomeActivity
import com.example.taller3_firebase.Usuario
import com.example.taller3_firebase.databinding.ActivityListaUsuarioBinding
import com.google.firebase.database.*

class ListaUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaUsuarioBinding
    private lateinit var databaseRef: DatabaseReference
    private val listaUsuariosDisponibles = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        listarUsuarios()

        val adapter = UsuarioAdapter(this, listaUsuariosDisponibles)
        binding.listView.adapter = adapter

        binding.back.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    private fun listarUsuarios() {
        // Lee los datos de la base de datos
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaUsuariosDisponibles.clear()

                for (usuarioSnapshot in snapshot.children) {
                    val name = usuarioSnapshot.child("name").getValue(String::class.java) ?: "Sin nombre"
                    val correo = usuarioSnapshot.child("email").getValue(String::class.java) ?: "Sin mail"
                    val disponible = usuarioSnapshot.child("disp").getValue(Boolean::class.java) ?: false

                    if (disponible) {
                        val usuario = Usuario(name, correo, disponible)
                        listaUsuariosDisponibles.add(usuario)
                    }
                }

                (binding.listView.adapter as UsuarioAdapter).notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaUsuarioActivity, "Error al leer los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
