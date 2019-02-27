package com.pmdm.sauky.mensajefirebase

import java.util.*

/**
 * Para guardar los valores que quiero introducir/actualizar en la base de datos
 * Contiene un HashMap con los datos, ya que las funciones que utilizaré necesitan como parámetro
 * un HashMap
 */
data class Datos(var token: String = "", var nombre: String = "", var numeroJugador: Int=5 ,var numeroIA: Int=9, var hora: Date = Date()) {
    // contenedor para actualizar los datos
    val miHashMapDatos = HashMap<String, Any>()

    /**
     * Mete los datos del objeto en el HashMap
     */
    fun crearHashMapDatos() {
        miHashMapDatos.put("token", token)
        miHashMapDatos.put("nombre", nombre)
        miHashMapDatos.put("numeroJugador", numeroJugador)
        miHashMapDatos.put("numeroIA", numeroIA)
        miHashMapDatos.put("hora", hora)
    }
}