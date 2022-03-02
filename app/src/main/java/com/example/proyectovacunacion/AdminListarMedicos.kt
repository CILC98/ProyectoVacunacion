package com.example.proyectovacunacion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AdminListarMedicos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_listar_medicos)
        this.setTitle("Médicos")
    }
}