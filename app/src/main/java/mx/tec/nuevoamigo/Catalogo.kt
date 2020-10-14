package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_catalogo.*
import mx.tec.nuevoamigo.perro.model.Perro
import mx.tec.nuevoamigo.perro.adapter.PerroAdapter

class Catalogo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)
        val datos = arrayListOf(
            Perro("YuriLoka", "Disponible", R.drawable.logo),
            Perro("Pug feo", "Adoptado", R.drawable.logo)
        )
        val elementoAdapter = PerroAdapter(this@Catalogo, R.layout.layout_elemento_perro, datos)
        listaPerro.adapter= elementoAdapter

        listaPerro.setOnClickListener {
            var i = Intent(this, InfoPerritoOtro::class.java)
            startActivity(i)
        }
    }
}