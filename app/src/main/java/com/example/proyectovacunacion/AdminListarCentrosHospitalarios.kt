package com.example.proyectovacunacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminListarCentrosHospitalarios : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var provinciasLista = arrayListOf<LugarGeografico>()
    var cantonesLista = arrayListOf<LugarGeografico>()
    var parroquiasLista = arrayListOf<LugarGeografico>()
    var centroHospitalario = ArrayList<CentroHospitalario>()
    var centroHospitalarioID = ArrayList<String>()
    var posP = 0
    var posC = 0
    var posPa = 0
    var itemSeleccionado = 0
    val CODIGO_RESPUESTA_INTENT_EXPLICITO = 401
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_listar_centros_hospitalarios)
        this.setTitle("Centros Hospitalarios")
        poblarSpinnersProvincia()

        val listViewCentroHospitalario = findViewById<ListView>(R.id.lv_listar_centro_hospitalario)
        registerForContextMenu(listViewCentroHospitalario)
        val btnAgregarCentrosHospitalario = findViewById<ImageView>(R.id.img_listar_vacuna_agregar_vacuna)
        btnAgregarCentrosHospitalario
            .setOnClickListener {
                abrirActividad(AdminAgregarCentroHospitalario::class.java)
            }
    }

    fun poblarSpinnersProvincia(){
        val db = Firebase.firestore
        val refLugarG = db.collection("lugarGeografico")
        val spinnerProvincia = findViewById<Spinner>(R.id.sp_listarch_provincia)
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
        val spinnerProvincia = findViewById<Spinner>(R.id.sp_listarch_provincia)
        val spinnerCanton = findViewById<Spinner>(R.id.sp_listarch_canton)
        val spinnerParroquia = findViewById<Spinner>(R.id.sp_listarch_parroquia)
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
                    poblarListViewCentros()
                }
            }
        }
    }
    fun actualizarSpinnerCanton(){
        val db = Firebase.firestore
        val refLugarG = db.collection("lugarGeografico")
        val spinnerCanton = findViewById<Spinner>(R.id.sp_listarch_canton)

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
        val spinnerParroquia = findViewById<Spinner>(R.id.sp_listarch_parroquia)
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

    fun poblarListViewCentros(){
        centroHospitalario.clear()
        centroHospitalarioID.clear()
        val db = Firebase.firestore
        val refCentroHospitalario = db.collection("centroHospitalario")
        refCentroHospitalario
            .whereEqualTo("lugarGeografico.canton",cantonesLista.get(posC).nombre)
            .whereEqualTo("lugarGeografico.parroquia",parroquiasLista.get(posPa).nombre)
            .whereEqualTo("lugarGeografico.provincia",provinciasLista.get(posP).nombre)
            .get()
            .addOnSuccessListener { centrosHospitalarios ->
                for(centro in centrosHospitalarios){
                    val centroCargado = centro.toObject(CentroHospitalario::class.java)
                    centroHospitalario.add(centroCargado)
                    centroHospitalarioID.add(centro.id)
                }
                actualizarListView()
            }
    }
    fun actualizarListView(){
        val listviewCentroHospitalario = findViewById<ListView>(R.id.lv_listar_centro_hospitalario)
        val adaptador = ArrayAdapter(
            this,//Contexto
            android.R.layout.simple_list_item_1,//Layout
            centroHospitalario//Arreglo
        )
        listviewCentroHospitalario.adapter = adaptador
        adaptador.notifyDataSetChanged()
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
    fun abrirActividad(clase : Class<*>){
        val intent = Intent(
            this,
            clase
        )
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if(cantonesLista.size != 0){
            poblarListViewCentros()
        }

    }
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_listarch,menu)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val id = info.position
        itemSeleccionado = id

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        var db = Firebase.firestore
        var refPedido = db.collection("centroHospitalario")
        val id = centroHospitalarioID.get(itemSeleccionado)
        Log.i("ID del documento", id)
        return when (item?.itemId){
            R.id.menu_listarch_editar ->{
                val intent = Intent(this,AdminEditarCenntroHospitaladio::class.java)
                intent.putExtra("id",id)
                intent.putExtra("posP",posP)
                intent.putExtra("posC",posC)
                intent.putExtra("posPa",posPa)
                intent.putExtra("codigo",centroHospitalario.get(itemSeleccionado).id)
                intent.putExtra("nombre",centroHospitalario.get(itemSeleccionado).nombre)
                abrirActividadP(intent)
                return true
            }
            R.id.menu_listarch_anadir_vacunas->{
                val intent = Intent(this,AdminListarVacunaCentroHospitalario::class.java)
                intent.putExtra("id",id)
                abrirActividadP(intent)
                return true
            }

            R.id.menu_listarch_eliiminar ->{
                refPedido
                    .document(id)
                    .delete()
                    .addOnSuccessListener {
                        centroHospitalario.removeAt(itemSeleccionado)
                        centroHospitalarioID.removeAt(itemSeleccionado)
                        actualizarListView()
                    }
                return true
            }
            else -> return super.onContextItemSelected(item)
        }


    }
    fun abrirActividadP(intent : Intent){
        startActivityForResult(intent,CODIGO_RESPUESTA_INTENT_EXPLICITO)
    }
}