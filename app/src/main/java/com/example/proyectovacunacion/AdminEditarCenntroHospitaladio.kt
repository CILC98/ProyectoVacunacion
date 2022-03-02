package com.example.proyectovacunacion

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminEditarCenntroHospitaladio : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var provinciasLista = arrayListOf<LugarGeografico>()
    var cantonesLista = arrayListOf<LugarGeografico>()
    var parroquiasLista = arrayListOf<LugarGeografico>()
    var posP = 0
    var posC = 0
    var posPa = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_editar_cenntro_hospitaladio)
        this.setTitle("Editar Centro Hospitalario")
        val id = intent.getStringExtra("id")
        posP = intent.getIntExtra("posP",0)
        posC = intent.getIntExtra("posC",0)
        posPa = intent.getIntExtra("posPa",0)
        findViewById<EditText>(R.id.et_editar_centro_hospitalario_nombre)
            .setText(intent.getStringExtra("nombre"))
        findViewById<EditText>(R.id.et_editar_centro_hospitalario_codigo)
            .setText(intent.getStringExtra("codigo"))
        poblarSpinnersProvincia()

        val btnAgregarCentroHospitalario = findViewById<Button>(R.id.btn_editar_centro_hospitalario)
        btnAgregarCentroHospitalario
            .setOnClickListener {
                val provincia = provinciasLista.get(posP).nombre
                val canton = cantonesLista.get(posC).nombre
                val parroquia = parroquiasLista.get(posPa).nombre
                val codigo = findViewById<EditText>(R.id.et_editar_centro_hospitalario_codigo).text.toString()
                val nombre = findViewById<EditText>(R.id.et_editar_centro_hospitalario_nombre).text.toString()
                var info = findViewById<TextView>(R.id.tv_info_editar_centro_hospitalario)
                if(codigo != "" && nombre != ""){
                    var nuevoCentrosHospitalario = CentroHospitalario()
                    nuevoCentrosHospitalario.nombre = nombre
                    nuevoCentrosHospitalario.id = codigo
                    nuevoCentrosHospitalario.lugarGeografico!!.parroquia = parroquia
                    nuevoCentrosHospitalario.lugarGeografico!!.provincia = provincia
                    nuevoCentrosHospitalario.lugarGeografico!!.canton = canton
                    val db = Firebase.firestore
                    val refCentroHospitalario = db.collection("centroHospitalario")
                    refCentroHospitalario
                        .document(id!!)
                        .update(
                            mapOf(
                                "nombre" to nuevoCentrosHospitalario.nombre,
                                "id" to nuevoCentrosHospitalario.id,
                                "lugarGeografico" to nuevoCentrosHospitalario.lugarGeografico
                            )
                        ).addOnSuccessListener {
                            info.setTextColor(Color.GREEN)
                            info.setText("Centro Hospitalario Actualizado")
                        }.addOnFailureListener {
                            info.setTextColor(Color.RED)
                            info.setText("No se pudo actualizar")
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
        val spinnerProvincia = findViewById<Spinner>(R.id.sp_editarch_provincia)
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
                    spinnerProvincia.setSelection(posP)
                }
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spinnerProvincia = findViewById<Spinner>(R.id.sp_editarch_provincia)
        val spinnerCanton = findViewById<Spinner>(R.id.sp_editarch_canton)
        val spinnerParroquia = findViewById<Spinner>(R.id.sp_editarch_parroquia)
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
        val spinnerCanton = findViewById<Spinner>(R.id.sp_editarch_canton)

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
                    spinnerCanton.setSelection(posC)
                    // actualizarSpinnerParroquias()
                }


            }
    }

    fun actualizarSpinnerParroquias(){
        val db = Firebase.firestore
        val refLugarG = db.collection("lugarGeografico")
        val spinnerParroquia = findViewById<Spinner>(R.id.sp_editarch_parroquia)
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
                    spinnerParroquia.setSelection(posPa)
                }
            }
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}