package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_catalogo.*
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.activity_perfil_usuario.*
import mx.tec.nuevoamigo.perro.adapter.PerroAdapter
import mx.tec.nuevoamigo.perro.adapter.PerroMainAdapter
import mx.tec.nuevoamigo.perro.model.Perro
import mx.tec.nuevoamigo.perro.model.PerroMain

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

        var user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        val ubicacionUser = intent.getStringExtra("Ubicacion")
        var fotoUser:String?=null

        if (user != null) {
            fotoUser = user.photoUrl.toString()
            Picasso.get().load("$fotoUser?type=large").into(imgPersonaMain)
        }

        var datos= mutableListOf<PerroMain>()
        db.collection("Persona").whereEqualTo("Ciudad",ubicacionUser)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    db.collection("Perro").whereEqualTo("idPersona",document.id)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                Log.d("TAG", "${document.id} => ${document.data}")

                                datos.add(PerroMain(document.data!!["Nombre"].toString(), document.data!!["Raza"].toString(), document.data!!["Edad"].toString(),
                                    document.data!!["Sexo"].toString(), document.data!!["Imagen"].toString()))
                            }
                        }
                }
            }



        val elementoAdapter = PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos)
        rvLista.layoutManager = LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL,true)
        rvLista.setHasFixedSize(true)

        rvLista.adapter= elementoAdapter


        /*
        map.setOnClickListener{
            var i = Intent(this@MainPage, Catalogo::class.java)
            startActivity(i)
        }*/

    }



}