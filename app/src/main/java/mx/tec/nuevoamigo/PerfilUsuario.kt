package mx.tec.nuevoamigo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_perfil_usuario.*
import mx.tec.nuevoamigo.persona.model.Persona

class PerfilUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)
        var user = FirebaseAuth.getInstance().currentUser
        var emailUser: String = ""
        var uid: String = ""
        var nameUser: String =""
        var photoUser: String? = null
        val db = FirebaseFirestore.getInstance()
        if (user != null) {
            // Name, email address, and profile photo Url
            emailUser = user.email!!
            nameUser = user.displayName!!
            uid = user.uid!!
            photoUser = user.photoUrl.toString()
        }
        db.collection("Persona").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.data == null) {
                    Log.d("Persona NO encontrada", "DocumentSnapshot data: ${document!!.data}")

                } else {
                    Log.d("Persona encontrada", photoUser.toString())
                    Picasso.get().load("$photoUser?type=large").into(imgPerfilU)
                    txtNombreU.text = document.data!!["Nombre"].toString()
                    txtDireccionU.text = document.data!!["Ciudad"].toString()
                    txtTelefono.text =  document.data!!["Telefono"].toString()
                    txtHorario.text =  document.data!!["HorarioAtencion"].toString()
                }
            }
        btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val i= Intent(this, MainActivity::class.java)
            i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
        btnEditarU.setOnClickListener {
            val i= Intent(this, EditarUsuario::class.java)
            startActivity(i)
        }
    }
}