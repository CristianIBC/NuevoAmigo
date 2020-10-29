package mx.tec.nuevoamigo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_page.*
import mx.tec.nuevoamigo.perro.adapter.PerroMainAdapter
import mx.tec.nuevoamigo.perro.model.PerroMain

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var datos= mutableListOf<PerroMain>()


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


        var fotoUser:String?=null

        val ubicUser: String? = intent.getStringExtra("Ubicacion")
        Log.d("ubic",ubicUser + "Hola")


        if (user != null) {
            fotoUser = user.photoUrl.toString()
            Picasso.get().load("$fotoUser?type=large").into(imgPersonaMain)
        }


        btnFiltrar.setOnClickListener {
            if(spinnerOpciones2.selectedItem.toString()=="Opciones..." && spinnerOpciones.selectedItem.toString()=="Opciones..."){
                return@setOnClickListener
            }else if(spinnerOpciones.selectedItem.toString()!="Opciones..." && spinnerOpciones2.selectedItem.toString()=="Opciones..."){
                datos.clear()
                db.collection("Persona").whereEqualTo("Ciudad",ubicUser).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("Perrito").whereEqualTo("idPersona",document.id).whereEqualTo("estado", "Disponible").whereEqualTo("tamaño",spinnerOpciones.selectedItem.toString() )
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        Log.d("TAG", "${document.id} => ${document.data}")
                                        datos.add(
                                            PerroMain(
                                                document.data!!["nombre"].toString(),
                                                document.data!!["raza"].toString(),
                                                document.data!!["edad"].toString(),
                                                document.data!!["sexo"].toString(),
                                                document.data!!["imagen"].toString()
                                            )
                                        )
                                        Log.d("TAG", datos.toString())
                                    }
                                    val elementoAdapter =
                                        PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos)

                                    elementoAdapter.notifyDataSetChanged()

                                    rvLista.layoutManager =
                                        LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL, true)
                                    rvLista.setHasFixedSize(true)
                                    rvLista.adapter = elementoAdapter
                                }
                        }
                    }

            }else if(spinnerOpciones.selectedItem.toString()=="Opciones..." && spinnerOpciones2.selectedItem.toString()!="Opciones..."){
                datos.clear()
                db.collection("Persona").whereEqualTo("Ciudad",ubicUser).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("Perrito").whereEqualTo("idPersona",document.id).whereEqualTo("estado", "Disponible").whereEqualTo("sexo",spinnerOpciones2.selectedItem.toString() )
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        Log.d("TAG", "${document.id} => ${document.data}")
                                        datos.add(
                                            PerroMain(
                                                document.data!!["nombre"].toString(),
                                                document.data!!["raza"].toString(),
                                                document.data!!["edad"].toString(),
                                                document.data!!["sexo"].toString(),
                                                document.data!!["imagen"].toString()
                                            )
                                        )
                                        Log.d("TAG", datos.toString())
                                    }
                                    val elementoAdapter =
                                        PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos)
                                    elementoAdapter.notifyDataSetChanged()
                                    rvLista.layoutManager =
                                        LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL, true)
                                    rvLista.setHasFixedSize(true)
                                    rvLista.adapter = elementoAdapter
                                }
                        }
                    }
            }else if(spinnerOpciones.selectedItem.toString()!="Opciones..." && spinnerOpciones2.selectedItem.toString()!="Opciones..."){
                datos.clear()
                db.collection("Persona").whereEqualTo("Ciudad",ubicUser).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("Perrito").whereEqualTo("idPersona",document.id).whereEqualTo("estado", "Disponible").whereEqualTo("tamaño",spinnerOpciones.selectedItem.toString()).whereEqualTo("sexo",spinnerOpciones2.selectedItem.toString() )
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        Log.d("TAG", "${document.id} => ${document.data}")
                                        datos.add(
                                            PerroMain(
                                                document.data!!["nombre"].toString(),
                                                document.data!!["raza"].toString(),
                                                document.data!!["edad"].toString(),
                                                document.data!!["sexo"].toString(),
                                                document.data!!["imagen"].toString()
                                            )
                                        )
                                        Log.d("TAG", datos.toString())
                                    }
                                    val elementoAdapter =
                                        PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos)
                                    elementoAdapter.notifyDataSetChanged()
                                    rvLista.layoutManager =
                                        LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL, true)
                                    rvLista.setHasFixedSize(true)
                                    rvLista.adapter = elementoAdapter
                                }
                        }
                    }
            }
        }


    //CATALOGO PRINCIPAL, :C






        db.collection("Persona").whereEqualTo("Ciudad",ubicUser).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                        db.collection("Perrito").whereEqualTo("idPersona", document.id)
                            .whereEqualTo("estado", "Disponible")
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    Log.d("TAG", "${document.id} => ${document.data}")

                                    datos.add(
                                        PerroMain(
                                            document.data!!["nombre"].toString(),
                                            document.data!!["raza"].toString(),
                                            document.data!!["edad"].toString(),
                                            document.data!!["sexo"].toString(),
                                            document.data!!["imagen"].toString()
                                        )
                                    )
                                    Log.d("TAG", datos.toString())
                                }
                                val elementoAdapter =
                                    PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos)
                                rvLista.layoutManager =
                                    LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL, true)
                                rvLista.setHasFixedSize(true)
                                rvLista.adapter = elementoAdapter
                            }

                }
            }
    }
}