package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
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
            Perro("1ThwPzXtOvG5sWbVEuD5","YuriLoka", "Disponible", R.drawable.yuriloka),
            Perro("1ThwPzXtOvG5sWbVEuD5","Puki", "Adoptado", R.drawable.puki)
        )
        val elementoAdapter = PerroAdapter(this@CatalogoPropio, R.layout.layout_elemento_perro, datos)
        listaPerro.adapter= elementoAdapter

        listaPerro.setOnItemClickListener { parent, view, position, id ->
            var i = Intent(this, InfoPerrita::class.java)
            i.putExtra("idPerro",datos[position].id)
            startActivity(i)
        }
        btnMas.setOnClickListener {
            var user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                var i = Intent(this, RegistrarPerrita::class.java)
                i.putExtra("idPersona", user.uid)
                startActivity(i)
            }
        }
    }
}