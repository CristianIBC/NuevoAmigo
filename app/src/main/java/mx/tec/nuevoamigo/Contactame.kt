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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_contactame.*
import mx.tec.nuevoamigo.utils.Credentials
import java.net.URLEncoder
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class Contactame : AppCompatActivity() {
    //MAIL SENDER
    lateinit var appExecutors: AppExecutors
    var Email = ""
    /*var nombre: TextView? = null
    var nombrePerrito:String? =""*/
    override fun onCreate(savedInstanceState: Bundle?) {
        //MAIL VARIABLE
        appExecutors = AppExecutors()
        //MAIL VARIABLE END

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactame)

        var user = FirebaseAuth.getInstance().currentUser
        var nameUser: String =""
        if (user != null) {
            // Name, email address, and profile photo Url

            nameUser = user.displayName!!

        }
        val id = intent.getStringExtra("idPersona")
        val nombrePerrito = intent.getStringExtra("nombrePerrito")

        val nombre = findViewById<TextView>(R.id.txtNombreC)
        val horarioAtencion = findViewById<TextView>(R.id.tVHorario)
        val tipo = findViewById<TextView>(R.id.tVTipo)
        val ciudad = findViewById<TextView>(R.id.txtCiudadC)
        var telefono = ""
        var personita = ""


        val db = FirebaseFirestore.getInstance()
        db.collection("Persona").document(id!!)
            .get()
            .addOnSuccessListener { document ->
                if(document!==null){
                    personita=document["Nombre"].toString()
                    nombre?.text = document["Nombre"].toString()
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

            val mensaje ="Hola ${nombre?.text.toString()}, soy ${nameUser.toString()}, te contacté por medio de la aplicación Nuevo Amigo. Estoy interesado/a en adoptar a tu perrito/a ${nombrePerrito.toString()}."
            val url = "https://api.whatsapp.com/send?phone=+52 $telefono"+"&text="+URLEncoder.encode(mensaje, "UTF-8");

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
            sendEmail(personita, nombrePerrito.toString())
        }
    }


    //EMAIL
    private fun sendEmail(nombProp:String, nombPerr:String){
        val cred= Credentials()
        appExecutors.diskIO().execute {
            val props = System.getProperties()
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")

            val session =  Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(cred.correo, cred.contra)
                    }
                })

            try {
                //Creating MimeMessage object
                val mm = MimeMessage(session)
                //Setting sender address
                mm.setFrom(InternetAddress(cred.correo))
                //Adding receiver
                mm.addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(Email)
                )
                //Adding subject
                mm.subject = "Adoptar perrito"
                //Adding message
                mm.setText("Buen día $nombProp \n " +
                        "Somos del equipo de Nuevo Amigo. Enviamos este correo para que revise la aplicación de Nuevo Amigo. \n " +
                        "Parece que alguien está interesado en uno de sus perritos puestos en adopción, llamado $nombPerr. \n " +
                        "No olvides responder al posible adoptante lo más antes posible, para que lleguen a un acuerdo sobre la adopción de $nombPerr. \n " +
                        "Siempre estamos al tanto de nuestros usuarios, y de nuestros amigos perrunos." +
                        "Atentamente,  \n " +
                        "El Equipo de Nuevo Amigo")


                //Sending email
                Transport.send(mm)

                appExecutors.mainThread().execute {
                    //Something that should be executed on main thread.
                }

            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }

}