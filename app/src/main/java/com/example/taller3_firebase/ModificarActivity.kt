package com.example.taller3_firebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3_firebase.databinding.ActivityModificarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ModificarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModificarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModificarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.btnSeleccionarImagen.setOnClickListener {
            seleccionarImagen()
        }

        binding.btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.ivImagenContacto.setImageURI(selectedImageUri)
        }
    }

    private fun guardarCambios() {
        val nombre = binding.etNombre.text.toString()
        val apellido = binding.etApellido.text.toString()
        val identificacion = binding.etNumeroIdentificacion.text.toString()
        val correo = binding.etCorreo.text.toString()
        val contrasena = binding.etContrasena.text.toString()
        val latitud = binding.etLatitud.text.toString()
        val longitud = binding.etLongitud.text.toString()

        val userId = auth.currentUser?.uid ?: return
        val userRef = database.getReference("users").child(userId)

        // Create updates map but only add non-empty or non-null fields
        val updates = mutableMapOf<String, Any?>()
        if (nombre.isNotEmpty()) updates["name"] = nombre
        if (apellido.isNotEmpty()) updates["lastname"] = apellido
        if (identificacion.isNotEmpty()) updates["identification"] = identificacion
        if (correo.isNotEmpty()) updates["email"] = correo
        if (latitud != null) updates["latitude"] = latitud
        if (longitud != null) updates["longitude"] = longitud

        // Update email and password in FirebaseAuth if they are not empty
        auth.currentUser?.let { currentUser ->
            if (correo.isNotEmpty()) currentUser.updateEmail(correo)
            if (contrasena.isNotEmpty()) currentUser.updatePassword(contrasena)
        }

        // Upload image if selected, else proceed with data update
        selectedImageUri?.let { uri ->
            val storageRef = storage.reference.child("imagenes/$userId.jpg")
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    updates["imagenUrl"] = url.toString()
                    actualizarDatos(userRef, updates)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        } ?: actualizarDatos(userRef, updates)
    }

    private fun actualizarDatos(userRef: DatabaseReference, updates: Map<String, Any?>) {
        userRef.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
            }
    }
}
