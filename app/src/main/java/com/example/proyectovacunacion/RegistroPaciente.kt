package com.example.proyectovacunacion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RegistroPaciente : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_paciente)
        this.setTitle("Registro Paciente")
    }
}