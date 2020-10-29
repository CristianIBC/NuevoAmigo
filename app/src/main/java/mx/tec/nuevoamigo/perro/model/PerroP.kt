package mx.tec.nuevoamigo.perro.model

import android.util.Log
import java.util.*

data class PerroP(
    var nombre: String,
    var descripcion: String,
    var estado: String,
    var idPersona: String,
    var imagen: Any,
    var imagenPerfil: Any,
    var raza: String,
    var sexo: String,
    var tamano: String,
    var edad: Long
) {
    constructor(): this("", "", "", "", "", "", "", "", "", 0)
    fun setear(mapa: Map<String, Any>?) {
        //Log.d("test", "fun Setear $mapa")
        if (mapa != null) {
            nombre = (mapa.get("nombre") as? String).toString()
            descripcion = mapa.get("descripcion") as String
            estado = mapa.get("estado") as String
            idPersona = mapa.get("idPersona") as String
            imagen = mapa.get("imagen") as Any
            imagenPerfil = mapa.get("imagenPerfil") as Any
            raza = mapa.get("raza") as String
            sexo = mapa.get("sexo") as String
            tamano = mapa.get("tamaño") as String
            edad = mapa.get("edad") as Long
        }
        else {
            Log.d("test", "fun Setear $mapa")
        }
    }
    fun convTomap():Map<String, Any>{
        val perrito: MutableMap<String, Any> = HashMap()
        perrito["nombre"] = nombre
        perrito["descripcion"] = descripcion
        perrito["estado"] = estado
        perrito["idPersona"] = idPersona
        perrito["imagen"] = imagen
        perrito["imagenPerfil"] = imagenPerfil
        perrito["raza"] = raza
        perrito["sexo"] = sexo
        perrito["tamaño"] = tamano
        perrito["edad"] = edad
        return perrito
    }
}