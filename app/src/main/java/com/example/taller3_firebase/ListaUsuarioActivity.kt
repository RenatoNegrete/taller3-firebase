package com.example.taller3_firebase

import UsuarioAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taller3_firebase.HomeActivity
import com.example.taller3_firebase.Usuario
import com.example.taller3_firebase.databinding.ActivityListaUsuarioBinding
import com.google.firebase.database.*

class ListaUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaUsuarioBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var usuarioAdapter: UsuarioAdapter
    private val listaUsuariosDisponibles = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        usuarioAdapter = UsuarioAdapter(listaUsuariosDisponibles) { usuario ->
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("nombre", usuario.nombre)
            intent.putExtra("correo", usuario.correo)
            intent.putExtra("disponible", usuario.disponible)
            startActivity(intent)
        }

        binding.recyclerViewUsuarios.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsuarios.adapter = usuarioAdapter

        cargarUsuariosDisponibles()
    }

    private fun cargarUsuariosDisponibles() {
        val usuariosRef = database.getReference("usuarios")

        usuariosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaUsuariosDisponibles.clear()
                for (userSnapshot in snapshot.children) {
                    val usuario = userSnapshot.getValue(Usuario::class.java)
                    if (usuario != null && usuario.disponible) {
                        listaUsuariosDisponibles.add(usuario)
                    }
                }
                usuarioAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ListaUsuariosActivity", "Error al cargar usuarios: ${error.message}")
            }
        })
    }
}
