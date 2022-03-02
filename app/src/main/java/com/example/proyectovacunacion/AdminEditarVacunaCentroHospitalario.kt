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
import org.w3c.dom.Text

class AdminEditarVacunaCentroHospitalario : AppCompatActivity() {
    var idVacuna = ""
    var vacunaCargada : Vacuna? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_editar_vacuna_centro_hospitalario)
        this.setTitle("Editar Vacuna Centro Hospitalario")

        val nombre = intent.getStringExtra("nombre")
        val codigo = intent.getStringExtra("codigo")
        val idC = intent.getStringExtra("idC")
        val idCV = intent.getStringExtra("idCV")
        val dosis = intent.getIntExtra("dosis", 0)

        obtenerVacuna(codigo!!,nombre!!)
        val etNombre = findViewById<EditText>(R.id.et_vacunaC_editar_nombre)
        etNombre.setText(nombre)
        val etDosis = findViewById<EditText>(R.id.et_vacunaC_editar_dosis)
        etDosis.setText(dosis.toString())
        val etCodigo = findViewById<EditText>(R.id.et_vacunaC_editar_codigo)
        etCodigo.setText(codigo)


        val btnActualizarVacuna = findViewById<Button>(R.id.btn_vacunaC_editar_vacuna)
        val tvInfo = findViewById<TextView>(R.id.tv_info_vacunaC)
        btnActualizarVacuna
            .setOnClickListener {
                if(etDosis.text.toString() != ""){
                    if(etDosis.text.toString().isDigitsOnly()){
                        if(etDosis.text.toString().toInt() <=vacunaCargada!!.dosis-dosis){
                            val db = Firebase.firestore
                            db
                                .collection("centroHospitalario")
                                .document(idC!!)
                                .collection("vacunas")
                                .document(idCV!!)
                                .update(mapOf(
                                    "dosis" to etDosis.text.toString().toInt()
                                ))
                                .addOnSuccessListener {
                                    db.collection("vacunas")
                                        .document(idVacuna)
                                        .update(
                                            mapOf(
                                                "dosisComprometidas" to vacunaCargada!!.dosisComprometidas-dosis + etDosis.text.toString().toInt()
                                            )
                                        ).addOnSuccessListener {
                                            tvInfo.setTextColor(Color.GREEN)
                                            tvInfo.setText("Actualización Realizada")
                                        }

                                }

                        }else{
                            tvInfo.setTextColor(Color.RED)
                            tvInfo.setText("No se puede registrar\nDosis disponibles ${vacunaCargada!!.dosis-dosis}")
                        }
                    }else{
                        tvInfo.setTextColor(Color.RED)
                        tvInfo.setText("Ingrese solo números")
                    }
                }else{
                    tvInfo.setTextColor(Color.RED)
                    tvInfo.setText("Llene todos los campos")
                }
            }
    }
    fun obtenerVacuna(codigo : String, nombre : String){
        val db = Firebase.firestore
        db.collection("vacunas")
            .whereEqualTo("nombre", nombre)
            .whereEqualTo("id",codigo)
            .get()
            .addOnSuccessListener { vacunas ->

                for(vacuna in vacunas){
                    idVacuna = vacuna.id
                    vacunaCargada = vacuna.toObject(Vacuna::class.java)
                }
            }
    }
}