package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_catalogo.*
import kotlinx.android.synthetic.main.activity_catalogo.listaPerro
import kotlinx.android.synthetic.main.activity_catalogo_propio.*
import mx.tec.nuevoamigo.perro.adapter.PerroAdapter
import mx.tec.nuevoamigo.perro.model.Perro

class CatalogoPropio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo_propio)
        val datos = arrayListOf(
            Perro("YuriLoka", "Disponible", R.drawable.logo),
            Perro("Pug feo", "Adoptado", R.drawable.logo)
        )
        val elementoAdapter = PerroAdapter(this@CatalogoPropio, R.layout.layout_elemento_perro, datos)
        listaPerro.adapter= elementoAdapter

        listaPerro.setOnItemClickListener { parent, view, position, id ->
            var i = Intent(this, InfoPerrita::class.java)
            startActivity(i)
        }
        btnMas.setOnClickListener {
            var i = Intent(this, RegistrarPerrita::class.java)
            startActivity(i)
        }
    }
}