package com.example.proyectovacunacion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PrincipalUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_usuario)
        this.setTitle("Usuario")
    }
}