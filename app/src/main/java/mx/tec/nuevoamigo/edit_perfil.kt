package mx.tec.nuevoamigo

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_perfil.*
import java.util.*
import kotlin.time.hours


class edit_perfil : AppCompatActivity() {
    var dt = Date()
    val c= Calendar.getInstance().time
    var hour= c.hours
    var minute= c.minutes
    var emailUser: String = ""
    var uid: String = ""
    var estadoUser: String = "Guerrero"
    var ciudadUser: String = "Acapulco"
    var nameUser: String =""
    val db = FirebaseFirestore.getInstance()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Name, email address, and profile photo Url
            emailUser = user.email!!
            nameUser = user.displayName!!
            uid = user.uid!!
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_perfil)
        var name= intent.getStringExtra("name")
        txtEmail.text =name

        btnHorarioInicio.setOnClickListener{

         TimePickerDialog(
             this,
             TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                 txtHoraInicio.text = "$hourOfDay:$minute"
             },
             hour,
             minute,
             true
         ).show()
        }

        btnHorarioFin.setOnClickListener {
           TimePickerDialog(
               this,
               TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                   txtHoraFin.text = "$hourOfDay:$minute"
               },
               hour,
               minute,
               true
           ).show()
        }
        btnGuardarPerfil.setOnClickListener {
            val user: MutableMap<String, Any> = HashMap()
            user["Nombre"] = nameUser
            user["Estado"] = estadoUser
            user["Ciudad"] = ciudadUser
            user["Email"] = emailUser
            user["Telefono"] = txtCelular.text.toString()
            user["HorarioAtencion"] = txtHoraInicio.text.toString() +"-"+ txtHoraFin.text.toString()
            db.collection("Persona").document(uid)
                .set(user)
                .addOnSuccessListener { Log.d(
                    "Persona registrada",
                    "DocumentSnapshot successfully written!"
                ) }
                .addOnFailureListener(OnFailureListener { e ->
                    Log.w(
                        "Error al guardar",
                        "Error adding document",
                        e
                    )
                })
            var i = Intent(this@edit_perfil, MainPage::class.java)
            startActivity(i)
        }

    }

    private fun getTime(){

        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        minute =cal.get(Calendar.MINUTE)
    }

    private fun pickTime(){

    }

}