package com.example.proyectovacunacion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PrincipalMedico : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_medico)
        this.setTitle("Médico")
    }
}