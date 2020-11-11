package mx.tec.nuevoamigo

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_contactame.*
import java.net.URLEncoder

class Contactame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactame)

        val id = intent.getStringExtra("idPersona")

        val nombre = findViewById<TextView>(R.id.txtNombreC)
        val horarioAtencion = findViewById<TextView>(R.id.tVHorario)
        val tipo = findViewById<TextView>(R.id.tVTipo)
        val ciudad = findViewById<TextView>(R.id.txtCiudadC)
        var Email = ""
        var telefono = ""


        val db = FirebaseFirestore.getInstance()
        db.collection("Persona").document(id!!)
            .get()
            .addOnSuccessListener { document ->
                if(document!==null){
                    nombre.text = document["Nombre"].toString()
                    horarioAtencion.text = document["HorarioAtencion"].toString()
                    if(document["IsAlbergue"].toString() == "false")
                        tipo.text = "Persona"
                    else
                        tipo.text = "Albergue"
                    ciudad.text = document["Ciudad"].toString()
                    Email = document["Email"].toString()
                    telefono = document["Telefono"].toString()
                }
                else{
                    Toast.makeText(this,"Hubo un error al recuperar el usuario, intentelo más tarde",Toast.LENGTH_SHORT).show()
                }
            }
        val btnWhats = findViewById<Button>(R.id.btnWha)
        btnWhats.setOnClickListener {


            val url = "https://api.whatsapp.com/send?phone=+52 $telefono"+"&text="+URLEncoder.encode("Hola ${nombre.text.toString()}, estoy interasado en adoptar a tu perrito", "UTF-8");

            val intent = Intent()
            intent.type = "text/plain"


            intent.setPackage("com.whatsapp")
            intent.setData(Uri.parse(url))


            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                var builder = AlertDialog.Builder(this)
                builder.setTitle("Whatsapp no instalado")
                builder.setMessage("Asegúrate de tener instalada la aplicación de whatsapp, para ejecutar está función.")
                builder.setPositiveButton("ENTENDIDO",
                    { dialogInterface: DialogInterface, i: Int -> })
                builder.show()
            }

            //el telefono ya esta en la variable telefono
        }

        val btnEmail = findViewById<Button>(R.id.btnCorreo)
        btnEmail.setOnClickListener {
            TODO("Mandar el correo")
            //el correo ya esta en la variable Email
        }
    }
}