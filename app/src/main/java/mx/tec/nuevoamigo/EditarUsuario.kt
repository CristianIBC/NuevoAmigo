package mx.tec.nuevoamigo

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_editar_usuario.*
import java.util.*

class EditarUsuario : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    val c= Calendar.getInstance().time
    var hour= c.hours
    var minute= c.minutes
    var uid: String = ""
    var emailUser: String = ""
    var nameUser: String =""
    var photoUser: String? = null
    var isAlbergue:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Name, email address, and profile photo Url
            emailUser = user.email!!
            nameUser = user.displayName!!
            uid = user.uid!!
            photoUser = user.photoUrl.toString()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)
        txtNombreEditar.text= nameUser
        //Recuperar datos del usuario de la base de datos
        db.collection("Persona").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.data == null) {
                    Log.d("Persona NO encontrada", "DocumentSnapshot data: ${document!!.data}")

                } else {
                    Log.d("Persona encontrada", photoUser.toString())
                    Picasso.get().load("$photoUser?type=large").into(imgPerfilEditar)
                    txtNombreEditar.text = document.data!!["Nombre"].toString()
                    txtDireccionEditar.setText(document.data!!["Ciudad"].toString())
                    txtCelularEditar.setText( document.data!!["Telefono"].toString())
                    switchAlbergueEditar.isChecked = document.data!!["IsAlbergue"].toString().toBoolean()
                    var horario = document.data!!["HorarioAtencion"].toString().split("-")
                    txtHoraInicioEditar.text= horario[0]
                    txtHoraFinEditar.text= horario[1]

                }
            }



        btnHorarioInicioEditar.setOnClickListener{

            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    txtHoraInicioEditar.text = "$hourOfDay:$minute"
                },
                hour,
                minute,
                true
            ).show()
        }
        btnHorarioFinEditar.setOnClickListener {
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    txtHoraFinEditar.text = "$hourOfDay:$minute"
                },
                hour,
                minute,
                true
            ).show()
        }
        btnGuardarEditar.setOnClickListener {
            val user: MutableMap<String, Any> = HashMap()
            user["Nombre"] = nameUser
            user["Ciudad"] = txtDireccionEditar.text.toString()
            user["Email"] = emailUser
            user["IsAlbergue"] = isAlbergue
            user["Telefono"] = txtCelularEditar.text.toString()
            user["HorarioAtencion"] = txtHoraInicioEditar.text.toString() +"-"+ txtHoraFinEditar.text.toString()
            db.collection("Persona").document(uid)
                .set(user)
                .addOnSuccessListener { Log.d(
                    "Persona actualizada",
                    "DocumentSnapshot successfully written!"
                ) }
                .addOnFailureListener(OnFailureListener { e ->
                    Log.w(
                        "Error al editar",
                        "Error updating document",
                        e
                    )
                })
            var i = Intent(this@EditarUsuario, MainPage::class.java)
            i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }

    fun onclick(view: View) {
        if(view.id == R.id.switchAlbergueEditar){
            if(switchAlbergueEditar.isChecked){
                isAlbergue = true
            }
        }
    }
}