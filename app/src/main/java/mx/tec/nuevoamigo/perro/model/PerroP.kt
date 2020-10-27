package mx.tec.nuevoamigo.perro.model

import android.util.Log
import com.google.firebase.firestore.DocumentReference

data class PerroP(var nombre: String, var descripcion:String, var estado: String, var idPersona: String, var imagen:Any, var imagenPerfil:Any, var raza:String, var sexo:String, var tamano:String, var edad: Long) {
    constructor(): this("","","","","","","","","",0)
    fun setear(mapa: Map<String,Any>?) {
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
}