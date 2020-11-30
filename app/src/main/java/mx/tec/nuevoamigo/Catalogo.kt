package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_catalogo.*
import mx.tec.nuevoamigo.perro.model.Perro
import mx.tec.nuevoamigo.perro.adapter.PerroAdapter

class Catalogo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        var user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        var datos=  mutableListOf<Perro>()
        val idPerrito =  intent.getStringExtra("idPerrito")
        db.collection("Perrito").document(idPerrito!!).get()
            .addOnSuccessListener {document ->
                if (document.data == null) {
                    Log.d(getString(R.string.perro_no_encontrado),
                        "DocumentSnapshot data: ${document!!.data}")

                } else {
                    Log.d(getString(R.string.perro_ya_registrado),
                        "DocumentSnapshot data: ${document!!.data}")
                    db.collection("Persona").document(document.data!!["idPersona"].toString()).get()
                        .addOnSuccessListener {
                            supportActionBar!!.title = getString(R.string.catalogo_de_perros_de)+ it.data!!["Nombre"].toString()
                        }
                    db.collection("Perrito").whereEqualTo("idPersona", document.data!!["idPersona"].toString()).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                Log.d("TAG", "${document.id} => ${document.data}")

                                datos.add(Perro(document.id,document.data!!["nombre"].toString(), document.data!!["estado"].toString(), document.data!!["imagenPerfil"].toString()))
                                Log.d("TAG",datos.toString() )

                            }
                            val elementoAdapter = PerroAdapter(this@Catalogo, R.layout.layout_elemento_perro, datos)
                            listaPerro.adapter= elementoAdapter
                        }
                }

            }


        listaPerro.setOnItemClickListener { parent, view, position, id ->
            var i = Intent(this, InfoPerritoOtro::class.java)
            i.putExtra("idPerrito", datos[position].id)
            startActivity(i)
        }
    }
}