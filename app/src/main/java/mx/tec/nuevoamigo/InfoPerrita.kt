package mx.tec.nuevoamigo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import mx.tec.nuevoamigo.perro.model.PerroP
import java.io.File


class InfoPerrita : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_perrita)
        val id = intent.getStringExtra("idPerro")

        val imgPerritoP = findViewById<ImageView>(R.id.imgPerfilPerroV)
        val imgPerroV = findViewById<ImageView>(R.id.imgPerro)
        val btnEliminar = findViewById<Button>(R.id.btnEliminarV)
        val nombre = findViewById<TextView>(R.id.nombrePerroV)
        val raza = findViewById<TextView>(R.id.razaPerroV)
        val descripcion = findViewById<TextView>(R.id.descripcionPerroV)
        val sexo = findViewById<TextView>(R.id.sexoPerroV)
        val edad = findViewById<TextView>(R.id.edadPerroV)
        val tamaño = findViewById<TextView>(R.id.tamañoPerroV)
        val estado = findViewById<TextView>(R.id.estadoPerroV)


        val db = FirebaseFirestore.getInstance()

        var perrito = PerroP()
        db.collection("Perrito").document(id!!)
            .get()
            .addOnSuccessListener { document ->
                if(document != null){
                    perrito.setear(document.data)
                    nombre.text = perrito.nombre
                    raza.text = "Raza:"+perrito.raza
                    descripcion.text = perrito.descripcion
                    sexo.text = "Sexo: "+perrito.sexo
                    if(perrito.edad>12)
                        edad.text="Edad: "+perrito.edad/12+" añitos"
                    else if(perrito.edad>0)
                        edad.text="Edad: "+perrito.edad+" meses"
                    else
                        edad.visibility = View.INVISIBLE
                    tamaño.text = "Tamaño: "+perrito.tamano
                    estado.text = perrito.estado
                }else{
                    Toast.makeText(this, "Hubo un error", Toast.LENGTH_LONG).show()
                }
            }

        btnEliminar.setOnClickListener {
            db.collection("Perrito").document(id)
                .delete()
                .addOnSuccessListener {
                    TODO("mandar al activity necesario")
                }
                .addOnFailureListener{
                    Toast.makeText(this,"Hubo un error",Toast.LENGTH_LONG).show()
                }
        }
    }
}