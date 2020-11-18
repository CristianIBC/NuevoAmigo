package mx.tec.nuevoamigo

import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_perfil_usuario.*
import mx.tec.nuevoamigo.persona.model.Persona

class PerfilUsuario :Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_perfil_usuario, container, false)
        var user = FirebaseAuth.getInstance().currentUser
        var emailUser: String = ""
        var uid: String = ""
        var nameUser: String =""
        var photoUser: String? = null
        val imgPerfilU = view.findViewById<ImageView>(R.id.imgPerfilU)
        val txtNombreU = view.findViewById<TextView>(R.id.txtNombreU)
        val txtDireccionU = view.findViewById<TextView>(R.id.txtDireccionU)
        val txtTelefono = view.findViewById<TextView>(R.id.txtTelefono)
        val btnCerrarSesion = view.findViewById<Button>(R.id.btnCerrarSesion)
        val txtHorario = view.findViewById<TextView>(R.id.txtHorario)
        val btnEditarU = view.findViewById<Button>(R.id.btnEditarU)

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
            val i= Intent(requireContext(), MainActivity::class.java)
            i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
        btnEditarU.setOnClickListener {
            val i= Intent(requireContext(), EditarUsuario::class.java)
            startActivity(i)
        }
        return view
    }
}