package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_catalogo.*
import kotlinx.android.synthetic.main.activity_catalogo.listaPerro
import kotlinx.android.synthetic.main.activity_catalogo_propio.*
import kotlinx.android.synthetic.main.activity_main_page.*
import mx.tec.nuevoamigo.perro.adapter.PerroAdapter
import mx.tec.nuevoamigo.perro.adapter.PerroMainAdapter
import mx.tec.nuevoamigo.perro.model.Perro
import mx.tec.nuevoamigo.perro.model.PerroMain

class CatalogoPropio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo_propio)
        var user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var datos=  mutableListOf<Perro>()
        db.collection("Perrito").whereEqualTo("idPersona", user?.uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")

                    datos.add(Perro(document.id,document.data!!["nombre"].toString(), document.data!!["estado"].toString(), document.data!!["imagenPerfil"].toString()))
                    Log.d("TAG",datos.toString() )

                }
                val elementoAdapter = PerroAdapter(this@CatalogoPropio, R.layout.layout_elemento_perro, datos)
                listaPerro.adapter= elementoAdapter
            }
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