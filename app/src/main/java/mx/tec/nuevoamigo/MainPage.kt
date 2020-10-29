package mx.tec.nuevoamigo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_catalogo.*
import kotlinx.android.synthetic.main.activity_edit_perfil.*
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.activity_perfil_usuario.*
import mx.tec.nuevoamigo.perro.adapter.PerroAdapter
import mx.tec.nuevoamigo.perro.adapter.PerroMainAdapter
import mx.tec.nuevoamigo.perro.adapter.RecyclerViewClickInterface
import mx.tec.nuevoamigo.perro.model.Perro
import mx.tec.nuevoamigo.perro.model.PerroMain
import java.util.*

class MainPage : AppCompatActivity() , RecyclerViewClickInterface {
    var datos= mutableListOf<PerroMain>()
    override fun onCreate(savedInstanceState: Bundle?) {
        var user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        var spinner1Selected = 0
        val spinnerOpciones = findViewById<Spinner>(R.id.spinnerHeight)

        ArrayAdapter.createFromResource(this,R.array.opcionesTamaño,android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOpciones.adapter = adapter
        }

        spinnerOpciones.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinner1Selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        var spinner2Selected = 0
        val spinnerOpciones2 = findViewById<Spinner>(R.id.spinnerSex)

        ArrayAdapter.createFromResource(this,R.array.opcionesSexo,android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOpciones2.adapter = adapter
        }
        btnTusPerros.setOnClickListener {
            var i = Intent(this@MainPage, CatalogoPropio::class.java)
            startActivity(i)
        }
        spinnerOpciones2.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinner2Selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        imgPersonaMain.setOnClickListener {
            var i = Intent(this@MainPage, PerfilUsuario::class.java)
            startActivity(i)
        }


    //CATALOGO PRINCIPAL, :C



        var fotoUser:String?=null

        val ubicUser: String? = intent.getStringExtra("Ubicacion")
        Log.d("ubic",ubicUser + "Hola")


        if (user != null) {
            fotoUser = user.photoUrl.toString()
            Picasso.get().load("$fotoUser?type=large").into(imgPersonaMain)
        }
        db.collection("Persona").whereEqualTo("Ciudad",ubicUser).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    db.collection("Perrito").whereEqualTo("idPersona",document.id).whereEqualTo("estado", "Disponible")
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                Log.d("TAG", "${document.id} => ${document.data}")

                                datos.add(PerroMain(document.id, document.data!!["nombre"].toString(), document.data!!["raza"].toString(), document.data!!["edad"].toString(),
                                    document.data!!["sexo"].toString(), document.data!!["imagen"].toString()))

                                Log.d("TAG",datos.toString() )

                            }
                            val elementoAdapter = PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos, this)
                            rvLista.layoutManager = GridLayoutManager(this@MainPage, 1, GridLayoutManager.VERTICAL, false)
                            rvLista.setHasFixedSize(true)
                            rvLista.adapter= elementoAdapter
                        }
                }
            }





    }

    override fun onItemClick(position: Int) {
        var i = Intent(this@MainPage, InfoPerritoOtro::class.java)
        i.putExtra("idPerrito",datos[position].id )
        startActivity(i)
    }

    override fun onLongItemClick(position: Int) {
        TODO("Not yet implemented")
    }


}