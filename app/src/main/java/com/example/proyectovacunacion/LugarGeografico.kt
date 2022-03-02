package com.example.proyectovacunacion

data class LugarGeografico(
    var lugarGPadre : String = "",
    var nivel : String = "",
    var nombre : String = ""
) {
    override fun toString(): String {
        return nombre
    }
}