package com.example.proyectovacunacion

data class Vacuna (
    var id : String = "",
    var nombre : String = "",
    var dosis : Int = 0,
    var dosisComprometidas : Int = 0

        ) {
    override fun toString(): String {
        return "Código: ${id}\nNombre: ${nombre}\nNúmero de dosis: ${dosis}\nDosis comprometidas: ${dosisComprometidas}"
    }
}