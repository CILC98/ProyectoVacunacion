package com.example.proyectovacunacion

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class MainActivity : AppCompatActivity() {
    val CODIGO_INICIO_SESION = 102
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val botonLogin = findViewById<Button>(R.id.btn_login)
        botonLogin.setOnClickListener {
            llamarLoginUsuario()
        }
    }

    fun llamarLoginUsuario(){
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls(
                    "https://example.com/terms.html",
                    "https://example.com/privacy.htmll"
                )
                .build(),
            CODIGO_INICIO_SESION
        )
    }
    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode){
            CODIGO_INICIO_SESION->{
                if(resultCode == Activity.RESULT_OK){
                    val usuario : IdpResponse? = IdpResponse.fromResultIntent(data)
                    val usuarioLogeado = FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                    if(usuario != null && usuarioLogeado != null){
                        val nombre = usuario.user.name
                        val email = usuario.email
                        if(usuario.isNewUser == true){
                            registrarUsuarioPorPrimeraVez(usuario)
                            Log.i("firebase-login","NUEVO USUARIO ")
                        }else{
                            val db = Firebase.firestore
                            val refUsuarios = db.collection("usuarios")
                            refUsuarios
                                .whereEqualTo("email",email)
                                .get()
                                .addOnSuccessListener { usuarios ->
                                    var usuarioCargado :Usuario? = null
                                    for(usuario in usuarios){
                                         usuarioCargado = usuario.toObject(Usuario::class.java)
                                    }
                                    if(usuarioCargado != null){
                                        if(usuarioCargado.rol.equals("Administrador")){
                                            abrirActividad(PrincipalAdministrador::class.java)
                                        }else if(usuarioCargado.rol.equals("Médico")){
                                            abrirActividad(PrincipalUsuario::class.java)
                                        }else if(usuarioCargado.rol.equals("Paciente")){
                                            abrirActividad(PrincipalUsuario::class.java)
                                        }
                                    }
                                }
                        }
                    }
                }else{
                    Log.i("firebase-login", "El usuario canceló")
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

    @SuppressLint("RestrictedApi")
    fun registrarUsuarioPorPrimeraVez (usuario : IdpResponse){
        val usuarioLogeado = FirebaseAuth
            .getInstance()
            .getCurrentUser()
        if(usuario.email != null && usuarioLogeado != null){
            //roles : ["usuario", "admin"]
            //uid

            //Se obtiene la referencia
            val db = Firebase.firestore
            //se crean los roles
            val rol = "Paciente"

            val nuevoUsuario = hashMapOf<String, Any>(
                "rol" to rol,
                "id" to usuarioLogeado.uid,
                "email" to usuario.email.toString(),
                "nombre" to usuario.user.name.toString()
                )
            val identificadorUsuario = usuario.email
            db.collection("usuarios")
                //A.- firestore crea un identificador
                //.add(nuevoUsuario)
                //A.- Yo seteo un identifcador
                .document(identificadorUsuario.toString())
                .set(nuevoUsuario)
                .addOnSuccessListener {
                    abrirActividad(RegistroPaciente::class.java)
                    Log.i("firebase-firestore","Se creo")
                }
                .addOnFailureListener{
                    Log.i("firebase-firestore","Falló")
                }

        }
    }
}