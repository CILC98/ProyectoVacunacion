package com.example.proyectovacunacion

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.text.isDigitsOnly
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminListarVacunaCentroHospitalario : AppCompatActivity(),
    AdapterView.OnItemSelectedListener {
    var vacunasSpinnerLista = arrayListOf<Vacuna>()
    var vacunaNombreLista = arrayListOf<String>()
    var vacunaListView = arrayListOf<Vacuna>()
    var vacunasID = arrayListOf<String>()
    var vacunaCentroID = arrayListOf<String>()
    var posV = 0
    var id = ""
    var itemSeleccionado = 0
    val CODIGO_RESPUESTA_INTENT_EXPLICITO = 401
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_listar_vacuna_centro_hospitalario)
        setTitle("Vacunas del Centro Hospitalario")
        id = intent.getStringExtra("id")!!

        val listViewVacuna = findViewById<ListView>(R.id.lv_vacunas_centro_hospitalario)
        registerForContextMenu(listViewVacuna)
        val btnAgregarVacuna = findViewById<Button>(R.id.btn_agregar_vacuna_centro_hospitalario)
        btnAgregarVacuna
            .setOnClickListener {
                val cantidad = findViewById<EditText>(R.id.et_cantidad_vacuna_centro_hospitalario).text.toString()
                val info = findViewById<TextView>(R.id.tv_info_vacuna_centro_hospitalario)
                if(cantidad != "" && cantidad.isDigitsOnly()){
                    if(cantidad.toInt() + vacunasSpinnerLista.get(posV).dosisComprometidas <= vacunasSpinnerLista.get(posV).dosis){
                        var vacunaNueva = vacunasSpinnerLista.get(posV).copy()
                        vacunaNueva.dosis = cantidad.toInt()
                        vacunaNueva.dosisComprometidas = 0

                        val db = Firebase.firestore
                        val refCentroHospitalario = db.collection("centroHospitalario").document(id)
                        refCentroHospitalario
                            .collection("vacunas")
                            .add(vacunaNueva)
                            .addOnSuccessListener {
                                vacunaListView.add(vacunaNueva)
                                actualizarListViewVacunas()
                                val dosisComprometidas = cantidad.toInt() + vacunasSpinnerLista.get(posV).dosisComprometidas
                                db.collection("vacunas").document(vacunasID.get(posV))
                                    .update( mapOf(
                                        "dosisComprometidas" to dosisComprometidas)
                                    )
                                    .addOnSuccessListener {
                                        val db = Firebase.firestore
                                        val refVacunas = db.collection("vacunas")
                                        refVacunas
                                            .document(vacunasID.get(posV))
                                            .get()
                                            .addOnSuccessListener { vacuna->
                                                vacunasSpinnerLista[posV].dosisComprometidas =
                                                    vacuna.toObject(Vacuna::class.java)!!.dosisComprometidas
                                        }.addOnSuccessListener {
                                            poblarListViewVacunas()
                                            }

                                    }
                                info.setTextColor(Color.GREEN)
                                info.setText("Vacuna Agregada")
                            }
                    }else{
                        info.setTextColor(Color.RED)
                        info.setText("No existen dosis sufiencies\nDosis disponibles : " +
                                "${vacunasSpinnerLista.get(posV).dosis
                                        -vacunasSpinnerLista.get(posV).dosisComprometidas }")
                    }

                }else{
                    info.setTextColor(Color.RED)
                    info.setText("Coloque la cantidad adecuadamente")
                }
            }
    }

    fun poblarSpinnerVacunas(){
        vacunasSpinnerLista.clear()
        vacunaNombreLista.clear()
        vacunasID.clear()
        val db = Firebase.firestore
        val refVacunas = db.collection("vacunas")
        refVacunas
            .get()
            .addOnSuccessListener { vacunas ->
                for(vacuna in vacunas){
                    val vacunaCargada = vacuna.toObject(Vacuna::class.java)
                    vacunasSpinnerLista.add(vacunaCargada)
                    vacunaNombreLista.add(vacunaCargada.nombre)
                    vacunasID.add(vacuna.id)
                }
                if(vacunasSpinnerLista != null){
                    actualizarVacunaSpinner()
                }
            }
    }
    fun poblarListViewVacunas(){
        vacunaListView.clear()
        vacunaCentroID.clear()
        val db = Firebase.firestore
        val refCentroHospitalario = db.collection("centroHospitalario").document(id)
        refCentroHospitalario
            .collection("vacunas")
            .get()
            .addOnSuccessListener { vacunas ->
                for(vacuna in vacunas){
                    val vacunaCargada = vacuna.toObject(Vacuna::class.java)
                    vacunaListView.add(vacunaCargada)
                    vacunaCentroID.add(vacuna.id)
                }
                if(vacunaListView != null){
                    actualizarListViewVacunas()
                }
            }
    }
    fun actualizarVacunaSpinner(){
        val spinnerVacuna = findViewById<Spinner>(R.id.sp_vacuna_centro_hospitalario)
        val adapterPa = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            vacunaNombreLista
        )
        spinnerVacuna.adapter = adapterPa
        spinnerVacuna.onItemSelectedListener = this
    }

    fun actualizarListViewVacunas(){
        val listviewVacunas = findViewById<ListView>(R.id.lv_vacunas_centro_hospitalario)
        val adaptador = ArrayAdapter(
            this,//Contexto
            android.R.layout.simple_list_item_1,//Layout
            vacunaListView//Arreglo
        )
        listviewVacunas.adapter = adaptador
        adaptador.notifyDataSetChanged()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spinnerVacuna = findViewById<Spinner>(R.id.sp_vacuna_centro_hospitalario)
        if(parent != null){
            if(parent.adapter != null){
                if(parent.adapter.equals(spinnerVacuna.adapter)){
                    posV = position
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_vacuna_centro_hospitalario,menu)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val id = info.position
        itemSeleccionado = id

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        var db = Firebase.firestore
        var refVacunasCentro = db.collection("centroHospitalario").document(id)

        Log.i("ID del documento", id)
        return when (item?.itemId){
            R.id.eliminar_vacuna_centro_hospitalario->{
                val dosisDespejadas = vacunaListView.get(itemSeleccionado).dosis
                refVacunasCentro
                    .collection("vacunas")
                    .document(vacunaCentroID.get(itemSeleccionado))
                    .delete()
                    .addOnSuccessListener {
                        val info = findViewById<TextView>(R.id.tv_info_vacuna_centro_hospitalario)
                        info.setTextColor(Color.GREEN)
                        info.setText("Vacuna Eliminada")
                        db.collection("vacunas")
                            .whereEqualTo("id",vacunaListView.get(itemSeleccionado).id)
                            .whereEqualTo("nombre",vacunaListView.get(itemSeleccionado).nombre)
                            .get()
                            .addOnSuccessListener { vacunas->
                                var vacunaC : Vacuna? = null
                                var id = ""
                                for(vacuna in vacunas){
                                    vacunaC = vacuna.toObject(Vacuna::class.java)
                                    id = vacuna.id
                                }
                                if(vacunaC != null){
                                    val dosisRealesC = vacunaC!!.dosisComprometidas - dosisDespejadas
                                    db.collection("vacunas")
                                        .document(id)
                                        .update(
                                            mapOf(
                                                "dosisComprometidas" to dosisRealesC
                                            )
                                        )
                                        .addOnSuccessListener {
                                            poblarSpinnerVacunas()
                                        }
                                }

                            }

                        vacunaListView.removeAt(itemSeleccionado)
                        actualizarListViewVacunas()
                    }
                return true
            }
            R.id.editar_vacuna_centro_hospitalario ->{
                var intent = Intent(
                    this,
                    AdminEditarVacunaCentroHospitalario::class.java)
                intent.putExtra("idC",id)
                intent.putExtra("idCV", vacunaCentroID.get(itemSeleccionado))
                intent.putExtra("nombre",vacunaListView.get(itemSeleccionado).nombre)
                intent.putExtra("codigo",vacunaListView.get(itemSeleccionado).id)
                intent.putExtra("dosis",vacunaListView.get(itemSeleccionado).dosis)
                abrirActividadP(intent)
                return true
            }
            else -> return super.onContextItemSelected(item)
        }


    }
    fun abrirActividadP(intent: Intent){
        startActivityForResult(intent, CODIGO_RESPUESTA_INTENT_EXPLICITO)
    }

    override fun onResume() {
        super.onResume()
        poblarSpinnerVacunas()
        poblarListViewVacunas()
    }
}