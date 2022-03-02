package com.example.proyectovacunacion

data class CentroHospitalario(
    var id :String = "",
    var nombre : String = "",
    var lugarGeografico: LugarGeograficoCompleto? = LugarGeograficoCompleto()


) {
    override fun toString(): String {
        return "Código: ${id}\nNombre: ${nombre}"
    }
}