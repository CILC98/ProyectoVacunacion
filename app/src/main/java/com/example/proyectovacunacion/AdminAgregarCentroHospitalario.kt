package com.example.proyectovacunacion

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminAgregarCentroHospitalario : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var provinciasLista = arrayListOf<LugarGeografico>()
    var cantonesLista = arrayListOf<LugarGeografico>()
    var parroquiasLista = arrayListOf<LugarGeografico>()
    var posP = 0
    var posC = 0
    var posPa = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_agregar_centro_hospitalario)
        this.setTitle("Agregar Centro Hospitalario")
        poblarSpinnersProvincia()

        val btnAgregarCentroHospitalario = findViewById<Button>(R.id.btn_agregar_centro_hospitalario)
        btnAgregarCentroHospitalario
            .setOnClickListener {
                val provincia = provinciasLista.get(posP).nombre
                val canton = cantonesLista.get(posC).nombre
                val parroquia = parroquiasLista.get(posPa).nombre
                val codigo = findViewById<EditText>(R.id.et_agregar_centro_hospitalario_codigo).text.toString()
                val nombre = findViewById<EditText>(R.id.et_agregar_centro_hospitalario_nombre).text.toString()
                var info = findViewById<TextView>(R.id.tv_info_agregar_centro_hospitalario)
                if(codigo != "" && nombre != ""){
                    var nuevoCentrosHospitalario = CentroHospitalario()
                    nuevoCentrosHospitalario.nombre = nombre
                    nuevoCentrosHospitalario.id = codigo
                    nuevoCentrosHospitalario.lugarGeografico!!.parroquia = parroquia
                    nuevoCentrosHospitalario.lugarGeografico!!.provincia = provincia
                    nuevoCentrosHospitalario.lugarGeografico!!.canton = canton
                    findViewById<EditText>(R.id.et_agregar_centro_hospitalario_codigo).text.clear()
                    findViewById<EditText>(R.id.et_agregar_centro_hospitalario_nombre).text.clear()
                    val db = Firebase.firestore
                    val refCentroHospitalario = db.collection("centroHospitalario")
                    refCentroHospitalario
                        .add(nuevoCentrosHospitalario)
                        .addOnSuccessListener {
                            Log.i("CH-Creación","Creación exitosa")
                            info.setTextColor(Color.GREEN)
                            info.setText("Centro Hospitalario Agregado")
                        }
                        .addOnFailureListener {
                            Log.i("CH-Creación","Creación falló")
                            info.setTextColor(Color.RED)
                            info.setText("No se pudo crear el centro hospitalario")
                        }
                }else{
                    info.setTextColor(Color.RED)
                    info.setText("Llene todos los campos")
                }

            }
    }
    fun poblarSpinnersProvincia(){
        val db = Firebase.firestore
        val refLugarG = db.collection("lugarGeografico")
        val spinnerProvincia = findViewById<Spinner>(R.id.sp_agregarch_provincia)
        refLugarG
            .whereEqualTo("nivel","Provincia")
            .get()
            .addOnSuccessListener { provincias ->
                for (provincia in provincias){
                    val provinciaCargada = provincia.toObject(LugarGeografico::class.java)
                    provinciasLista.add(provinciaCargada)
                }
                if(provinciasLista != null){
                    val adapterP = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        provinciasLista
                    )
                    spinnerProvincia.adapter = adapterP
                    spinnerProvincia.onItemSelectedListener = this
                }
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spinnerProvincia = findViewById<Spinner>(R.id.sp_agregarch_provincia)
        val spinnerCanton = findViewById<Spinner>(R.id.sp_agregarch_canton)
        val spinnerParroquia = findViewById<Spinner>(R.id.sp_agregarch_parroquia)
        if(parent != null){
            if(parent.adapter != null){
                if(parent.adapter.equals(spinnerProvincia.adapter)){
                    posP = position
                    cantonesLista.clear()
                    actualizarSpinnerCanton()
                }else if (parent.adapter.equals(spinnerCanton.adapter)){
                    posC = position
                    parroquiasLista.clear()
                    actualizarSpinnerParroquias()
                }else if (parent.adapter.equals(spinnerParroquia.adapter)){
                    posPa = position
                }
            }
        }
    }
    fun actualizarSpinnerCanton(){
        val db = Firebase.firestore
        val refLugarG = db.collection("lugarGeografico")
        val spinnerCanton = findViewById<Spinner>(R.id.sp_agregarch_canton)

        refLugarG
            .whereEqualTo("nivel", "Cantón")
            .whereEqualTo("lugarGPadre",provinciasLista.get(posP).nombre)
            .get()
            .addOnSuccessListener { cantones ->
                for (canton in cantones){
                    val cantonCargado = canton.toObject(LugarGeografico::class.java)
                    cantonesLista.add(cantonCargado)
                }
                if(cantonesLista != null){
                    val adapterC = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        cantonesLista
                    )
                    spinnerCanton.adapter = adapterC
                    spinnerCanton.onItemSelectedListener = this
                    // actualizarSpinnerParroquias()
                }


            }
    }

    fun actualizarSpinnerParroquias(){
        val db = Firebase.firestore
        val refLugarG = db.collection("lugarGeografico")
        val spinnerParroquia = findViewById<Spinner>(R.id.sp_agregarch_parroquia)
        refLugarG
            .whereEqualTo("nivel", "Parroquia")
            .whereEqualTo("lugarGPadre",cantonesLista.get(posC).nombre)
            .get()
            .addOnSuccessListener { parroquias ->
                for (parroquia in parroquias){
                    val parroquiaCargada = parroquia.toObject(LugarGeografico::class.java)
                    parroquiasLista.add(parroquiaCargada)
                }
                if(parroquiasLista != null){
                    val adapterPa = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        parroquiasLista
                    )
                    spinnerParroquia.adapter = adapterPa
                    spinnerParroquia.onItemSelectedListener = this
                }
            }
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}