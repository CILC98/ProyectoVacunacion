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

class AdminEditarVacuna : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_editar_vacuna)
        val id = intent.getStringExtra("id")
        findViewById<EditText>(R.id.et_Editar_vacuna_nombre)
            .setText(
                intent.getStringExtra("nombre")
            )
        findViewById<EditText>(R.id.et_Editar_vacuna_codigo)
            .setText(
                intent.getStringExtra("codigo")
            )
        findViewById<EditText>(R.id.et_Editar_vacuna_dosis)
            .setText(
                intent.getIntExtra("dosis",0).toString()
            )
        val btnEditarVacuna = findViewById<Button>(R.id.btn_listarv_Editar_vacuna)
        btnEditarVacuna
            .setOnClickListener {
                val info = findViewById<TextView>(R.id.tv_info_Editar_vacuna)
                val codigo = findViewById<EditText>(R.id.et_Editar_vacuna_codigo).text.toString()
                val nombre = findViewById<EditText>(R.id.et_Editar_vacuna_nombre).text.toString()
                val dosis = findViewById<EditText>(R.id.et_Editar_vacuna_dosis).text.toString()
                if(codigo != "" && nombre != "" && dosis.isDigitsOnly()){

                    val db = Firebase.firestore
                    val refVacuna = db.collection("vacunas")
                    refVacuna
                        .document(id!!)
                        .update(
                            mapOf(
                                "nombre" to nombre,
                                "id" to codigo,
                                "dosis" to dosis.toInt()
                            )
                        ).addOnSuccessListener {
                            info.setText("Edición exitosa")
                            info.setTextColor(Color.GREEN)
                        }
                        .addOnFailureListener {
                            info.setText("No se pudo editar")
                            info.setTextColor(Color.RED)
                        }
                }else{
                    info.setText("Llene todos los campos adecuadamente")
                    info.setTextColor(Color.RED)
                }
            }
    }
}