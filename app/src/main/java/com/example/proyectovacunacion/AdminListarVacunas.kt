package com.example.proyectovacunacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminListarVacunas : AppCompatActivity() {
    var vacunasLista = arrayListOf<Vacuna>()
    var vacunasListaID = arrayListOf<String>()
    var itemSeleccionado = 0
    val CODIGO_RESPUESTA_INTENT_EXPLICITO = 401
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_listar_vacunas)
        this.setTitle("Vacunas")

        val btnAgregarVacuna = findViewById<ImageView>(R.id.img_listar_vacuna_agregar_vacuna)

        btnAgregarVacuna
            .setOnClickListener {
                abrirActividad(AdminAgregarVacuna::class.java)
            }
        val listViewVacuna = findViewById<ListView>(R.id.lv_listar_vacuna)
        registerForContextMenu(listViewVacuna)
    }

    fun abrirActividad(clase : Class<*>){
        val intent = Intent(
            this,
            clase
        )
        startActivity(intent)
    }
    fun poblarListViewVacunas(){
        vacunasLista.clear()
        vacunasListaID.clear()
        val db = Firebase.firestore
        val refVacuna = db.collection("vacunas")
        refVacuna
            .get()
            .addOnSuccessListener { vacunas ->
                for(vacuna in vacunas){
                    val vacunaCargada = vacuna.toObject(Vacuna::class.java)
                    vacunasLista.add(vacunaCargada)
                    vacunasListaID.add(vacuna.id)
                }
                if(vacunasLista != null){
                    actaulizarListView()
                }

            }
    }
    fun actaulizarListView(){
        val listViewVacuna = findViewById<ListView>(R.id.lv_listar_vacuna)
        val adaptador = ArrayAdapter(
            this,//Contexto
            android.R.layout.simple_list_item_1,//Layout
            vacunasLista//Arreglo
        )
        listViewVacuna.adapter = adaptador
        adaptador.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        poblarListViewVacunas()
    }
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_listar_vacuna,menu)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        val id = info.position
        itemSeleccionado = id

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        var db = Firebase.firestore
        var refPedido = db.collection("centroHospitalario")
        val id = vacunasListaID.get(itemSeleccionado)
        Log.i("ID del documento", id)
        return when (item?.itemId){
            R.id.menu_listarv_editar_vacuna ->{
                val intent = Intent(this,AdminEditarVacuna::class.java)
                intent.putExtra("id",id)
                intent.putExtra("codigo",vacunasLista.get(itemSeleccionado).id)
                intent.putExtra("nombre",vacunasLista.get(itemSeleccionado).nombre)
                intent.putExtra("dosis",vacunasLista.get(itemSeleccionado).dosis)
                abrirActividadP(intent)
                return true
            }
            else -> return super.onContextItemSelected(item)
        }


    }
    fun abrirActividadP(intent : Intent){
        startActivityForResult(intent,CODIGO_RESPUESTA_INTENT_EXPLICITO)
    }
}