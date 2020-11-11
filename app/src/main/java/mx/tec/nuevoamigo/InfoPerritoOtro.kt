package mx.tec.nuevoamigo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_info_perrito_otro.*
import mx.tec.nuevoamigo.perro.adapter.FirebaseRequestHandler
import mx.tec.nuevoamigo.perro.model.PerroP

class InfoPerritoOtro : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_perrito_otro)
        val id = intent.getStringExtra("idPerrito")
        val imgPerritoP = findViewById<ImageView>(R.id.imgPerfilPerroVO)
        val imgPerroV = findViewById<ImageView>(R.id.imgPerroVO)
        val nombre = findViewById<TextView>(R.id.nombrePerroVO)
        val raza = findViewById<TextView>(R.id.razaPerroVO)
        val descripcion = findViewById<TextView>(R.id.descripcionPerroVO)
        val sexo = findViewById<TextView>(R.id.sexoPerroVO)
        val edad = findViewById<TextView>(R.id.edadPerroVO)
        val tamaño = findViewById<TextView>(R.id.tamañoPerroVO)
        val estado = findViewById<TextView>(R.id.estadoPerroVO)

        storage = Firebase.storage
        val db = FirebaseFirestore.getInstance()

        var perrito = PerroP()

        db.collection("Perrito").document(id!!)
            .get()
            .addOnSuccessListener { document ->
                if(document != null){
                    perrito.setear(document.data)
                    nombre.text = perrito.nombre
                    raza.text = "Raza: ${perrito.raza}"
                    descripcion.text = perrito.descripcion
                    sexo.text = "Sexo: ${perrito.sexo}"
                    if(perrito.edad>12)
                        edad.text="Edad: ${+perrito.edad/12} añitos"
                    else if(perrito.edad>0)
                        edad.text="Edad: ${perrito.edad} meses"
                    else
                        edad.visibility = View.INVISIBLE
                    tamaño.text = "Tamaño: ${perrito.tamano}"
                    estado.text = perrito.estado

                    //imagenes

                    var picassoInstance = Picasso.Builder(this)
                        .addRequestHandler(FirebaseRequestHandler())
                        .build()

                    //imagenes
                    val imageRefP = storage.getReferenceFromUrl("${perrito.imagenPerfil}")
                    picassoInstance.load("$imageRefP").placeholder(R.drawable.cargando_blanco).error(R.drawable.avatar).into(imgPerritoP)
                    val imageRefF = storage.getReferenceFromUrl("${perrito.imagen}")
                    picassoInstance.load("$imageRefP").placeholder(R.drawable.cargando_blanco).error(R.drawable.avatar).into(imgPerroV)
                    //end imagenes
                }else{
                    Toast.makeText(this, "Hubo un error", Toast.LENGTH_LONG).show()
                }
            }
        btnVerCatalogo.setOnClickListener {
            val i = Intent(this, Catalogo::class.java)
                i.putExtra("idPerrito",id)
            startActivity(i)
        }
        btnContactame.setOnClickListener {
            val i = Intent(this, Contactame::class.java)
            i.putExtra("idPersona",perrito.idPersona)
            startActivity(i)
        }
    }
}