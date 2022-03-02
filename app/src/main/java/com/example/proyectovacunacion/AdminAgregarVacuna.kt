package com.example.proyectovacunacion

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminAgregarVacuna : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_agregar_vacuna)

        val btnAgregarVacuna = findViewById<Button>(R.id.btn_listarv_agregar_vacuna)

        btnAgregarVacuna
            .setOnClickListener {
                val info = findViewById<TextView>(R.id.tv_info_agregar_vacuna)
                val codigo = findViewById<EditText>(R.id.et_agregar_vacuna_codigo).text.toString()
                val nombre = findViewById<EditText>(R.id.et_agregar_vacuna_nombre).text.toString()
                val dosis = findViewById<EditText>(R.id.et_agregar_vacuna_dosis).text.toString()
                if(codigo != "" && nombre != "" && dosis.isDigitsOnly()){
                    val vacunaNueva = Vacuna()
                    vacunaNueva.id = codigo
                    vacunaNueva.nombre = nombre
                    vacunaNueva.dosis = dosis.toInt()

                    val db = Firebase.firestore
                    val refVacuna = db.collection("vacunas")
                    refVacuna
                        .add(vacunaNueva)
                        .addOnSuccessListener {
                            info.setText("Vacuna Agregada")
                            info.setTextColor(Color.GREEN)
                            findViewById<EditText>(R.id.et_agregar_vacuna_codigo).text.clear()
                            findViewById<EditText>(R.id.et_agregar_vacuna_nombre).text.clear()
                            findViewById<EditText>(R.id.et_agregar_vacuna_dosis).text.clear()
                        }
                        .addOnFailureListener {
                            info.setText("No se pudo agregar la vacuna")
                            info.setTextColor(Color.RED)
                        }
                }else{
                    info.setText("Llene todos los campos adecuadamente")
                    info.setTextColor(Color.RED)
                }
            }
    }
}