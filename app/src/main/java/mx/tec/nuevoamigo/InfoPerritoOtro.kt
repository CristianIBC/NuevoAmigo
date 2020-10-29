package mx.tec.nuevoamigo

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import mx.tec.nuevoamigo.perro.model.PerroP

class InfoPerritoOtro : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_perrito_otro)

        val id = intent.getStringExtra("idPerro")


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
                    val gsReferencePerfil = storage.getReferenceFromUrl("${perrito.imagenPerfil}")
                    val ONE_MEGABYTE: Long = 1024 * 1027
                    gsReferencePerfil.getBytes(ONE_MEGABYTE*12).addOnSuccessListener {
                        val bmp = BitmapFactory.decodeByteArray(it,0, it.size)
                        imgPerritoP.setImageBitmap(bmp)
                    }
                    val gsReference = storage.getReferenceFromUrl("${perrito.imagen}")
                    gsReference.getBytes(ONE_MEGABYTE*12).addOnSuccessListener {
                        val bmp = BitmapFactory.decodeByteArray(it,0, it.size)
                        imgPerroV.setImageBitmap(bmp)
                    }
                    //end imagenes
                }else{
                    Toast.makeText(this, "Hubo un error", Toast.LENGTH_LONG).show()
                }
            }
    }
}