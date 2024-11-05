package com.example.taller3_firebase

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.taller3_firebase.databinding.ActivityCrearCuentaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import java.util.logging.Logger
import java.util.regex.Matcher
import java.util.regex.Pattern

class CrearCuentaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrearCuentaBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    private val TAG = MainActivity::class.java.name
    private val logger = Logger.getLogger(TAG)

    lateinit var emailEdit: EditText
    lateinit var passEdit: EditText
    lateinit var latEdit: EditText
    lateinit var longEdit: EditText

    private val LOCATION_PERMISSION_ID = 103
    var locationPerm = Manifest.permission.ACCESS_FINE_LOCATION

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data!!.data
            logger.info("Image loaded successfully")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val singUpbtn = binding.signUp
        val imagenbtn = binding.image
        val ubicacionbtn = binding.ubicacion
        emailEdit = binding.mail
        passEdit = binding.password
        latEdit = binding.latitud
        longEdit = binding.longitud

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mAuth = Firebase.auth

        imagenbtn.setOnClickListener {
            val pickGalleryImage = Intent(Intent.ACTION_PICK)
            pickGalleryImage.type = "image/*"
            galleryActivityResultLauncher.launch(pickGalleryImage)
        }

        ubicacionbtn.setOnClickListener {
            getCurrentLocation()
        }

        singUpbtn.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        val name = binding.name.text.toString()
        val apellido = binding.lastname.text.toString()
        val identificacion = binding.idNum.text.toString()
        val lat = latEdit.text.toString()
        val long = longEdit.text.toString()

        if (name.isEmpty() || apellido.isEmpty() || identificacion.isEmpty() || lat.isEmpty() || long.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEmailValid(email)) {
            Toast.makeText(this@CrearCuentaActivity, "Email is not a valid format", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                val userId = user!!.uid
                val database = FirebaseDatabase.getInstance().getReference("users")
                val userInfo = mapOf(
                    "email" to email,
                    "name" to name,
                    "lastname" to apellido,
                    "identification" to identificacion,
                    "latitude" to lat,
                    "longitude" to long,
                )
                database.child(userId).setValue(userInfo).addOnCompleteListener {
                    Toast.makeText(
                        this@CrearCuentaActivity,
                        String.format("The user %s is successfully registered", user.email),
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener { e ->
                    Toast.makeText(this@CrearCuentaActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }.addOnFailureListener(this) { e ->
            Toast.makeText(this@CrearCuentaActivity, e.message, Toast.LENGTH_LONG).show() }
    }

    private fun isEmailValid(emailStr: String?): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
        return matcher.find()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            // Si ya se tienen permisos, obtiene la última ubicación
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    latEdit.setText(location.latitude.toString())
                    longEdit.setText(location.longitude.toString())
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
                    logger.warning("No se pudo obtener la ubicación actual")
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
                logger.warning("Error al obtener la ubicación: ${e.message}")
            }
        }
    }
}