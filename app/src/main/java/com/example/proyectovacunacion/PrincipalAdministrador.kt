package com.example.proyectovacunacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PrincipalAdministrador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_administrador)
        this.setTitle("Administrador")
        setearUsuarioFirebase()

        val btnMedicos = findViewById<Button>(R.id.btn_listar_medicos)
        btnMedicos
            .setOnClickListener {
                abrirActividad(AdminListarMedicos::class.java)
            }
        val btnCentrosHospitalarios = findViewById<Button>(R.id.btn_listar_centros_hospitalarios)
        btnCentrosHospitalarios
            .setOnClickListener {
                abrirActividad(AdminListarCentrosHospitalarios::class.java)
            }
        val btnVacunas = findViewById<Button>(R.id.btn_listar_vacunas)
        btnVacunas
            .setOnClickListener {
                abrirActividad(AdminListarVacunas::class.java)
            }

    }

    fun setearUsuarioFirebase(){
        val instanciaAuth = FirebaseAuth.getInstance()
        val usuarioLocal = instanciaAuth.currentUser
        if(usuarioLocal != null){
            if(usuarioLocal.email != null){
                //buscar en el firestore el usuario y traerlo con todos los datos
                val db = Firebase.firestore
                val reference = db
                    .collection("usuarios")
                    .document(usuarioLocal.email.toString())
                reference
                    .get()
                    .addOnSuccessListener {
                        val usuarioCargado = it.toObject(Usuario::class.java)
                        if(usuarioCargado != null){
                            BAuthUsuario.usuario = Usuario(
                                usuarioCargado.email,
                                usuarioCargado.id,
                                usuarioCargado.rol,
                                usuarioCargado.nombre
                            )
                        }
                        Log.i("firebase-direstore", "Usuario cargado")
                        val tvIdentificacion = findViewById<TextView>(R.id.tv_identificacion_administrador)
                        val tvNombres= findViewById<TextView>(R.id.tv_nombre_administrador)
                        val tvCorreo = findViewById<TextView>(R.id.tv_email_administrador)

                        tvIdentificacion.setText(BAuthUsuario.usuario!!.id)
                        tvNombres.setText(BAuthUsuario.usuario!!.nombre)
                        tvCorreo.setText(BAuthUsuario.usuario!!.email)
                    }
                    .addOnFailureListener {
                        Log.i("firebase-direstore", "Fallo cargar usuario")
                    }
            }
        }
    }

    fun abrirActividad(clase : Class<*>){
        val intent = Intent(
            this,
            clase
        )
        startActivity(intent)

    }
}