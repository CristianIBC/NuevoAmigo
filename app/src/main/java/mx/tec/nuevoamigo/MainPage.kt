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
    //gps
    val PERMISSION_ID = 1010
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    //gps

    var datos= mutableListOf<PerroMain>()
    var ubicActual:String = ""

    val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        //gps
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        RequestPermission()
        getLastLocation()

        //--

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

        if (user != null) {
            fotoUser = user!!.photoUrl.toString()
            Picasso.get().load("$fotoUser?type=large").into(imgPersonaMain)
        }

        //BOTON DEL FILTRADO
        btnFiltrar.setOnClickListener {
            Log.d("Debug", ubicActual + "JIJOOOOOOOO")
            if(spinnerOpciones2.selectedItem.toString()=="Opciones..." && spinnerOpciones.selectedItem.toString()=="Opciones..."){
                datos.clear()
                db.collection("Persona").whereEqualTo("Ciudad",ubicActual).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("Perrito").whereEqualTo("idPersona",document.id).whereEqualTo("estado", "Disponible")
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        Log.d("TAG", "${document.id} => ${document.data}")
                                        datos.add(
                                            PerroMain(
                                                document.id,
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
                                        PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos, this)

                                    elementoAdapter.notifyDataSetChanged()

                                    rvLista.layoutManager =
                                        LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL, false)
                                    rvLista.setHasFixedSize(true)
                                    rvLista.adapter = elementoAdapter
                                }
                        }
                    }
            }else if(spinnerOpciones.selectedItem.toString()!="Opciones..." && spinnerOpciones2.selectedItem.toString()=="Opciones..."){
                datos.clear()
                db.collection("Persona").whereEqualTo("Ciudad",ubicActual).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
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
                                                document.id,
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
                                        PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos, this)

                                    elementoAdapter.notifyDataSetChanged()

                                    rvLista.layoutManager =
                                        LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL, false)
                                    rvLista.setHasFixedSize(true)
                                    rvLista.adapter = elementoAdapter
                                }
                        }
                    }

            }else if(spinnerOpciones.selectedItem.toString()=="Opciones..." && spinnerOpciones2.selectedItem.toString()!="Opciones..."){
                datos.clear()
                db.collection("Persona").whereEqualTo("Ciudad",ubicActual).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
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
                                                document.id,
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
                                        PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos, this)
                                    elementoAdapter.notifyDataSetChanged()
                                    rvLista.layoutManager =
                                        LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL, false)
                                    rvLista.setHasFixedSize(true)
                                    rvLista.adapter = elementoAdapter
                                }
                        }
                    }
            }else if(spinnerOpciones.selectedItem.toString()!="Opciones..." && spinnerOpciones2.selectedItem.toString()!="Opciones..."){
                datos.clear()
                db.collection("Persona").whereEqualTo("Ciudad",ubicActual).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
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
                                                document.id,
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
                                        PerroMainAdapter(this@MainPage, R.layout.act_recycler, datos, this)
                                    elementoAdapter.notifyDataSetChanged()
                                    rvLista.layoutManager =
                                        LinearLayoutManager(this@MainPage, LinearLayoutManager.VERTICAL, false)
                                    rvLista.setHasFixedSize(true)
                                    rvLista.adapter = elementoAdapter
                                }
                        }
                    }
            }
        }


    //CATALOGO PRINCIPAL, :C







    }

    override fun onItemClick(position: Int) {
        var i = Intent(this@MainPage, InfoPerritoOtro::class.java)
        i.putExtra("idPerrito",datos[position].id )
        startActivity(i)
    }

    override fun onLongItemClick(position: Int) {
        TODO("Not yet implemented")
    }



    //--------------------------------------------------GPS

    fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    var location: Location? = task.result
                    if(location == null){
                        NewLocationData()
                    }else{
                        Log.d("Debug:" ,"Your Location:"+ location.longitude)
                        ubicActual = getCityName(location.latitude,location.longitude)
                        Log.d("Debug", ubicActual)

                        db.collection("Persona").whereEqualTo("Ciudad",ubicActual).whereNotEqualTo(FieldPath.documentId(), user?.uid.toString())
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

                        //ubicacionUser = getCityName(location.latitude,location.longitude)
                        //Log.d("Debug:" ,ubicacionUser)
                    }
                }
            }else{
                Toast.makeText(this,"Prenda su GPS y reinicie la aplicación ", Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }


    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
        }
    }

    private fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the Permission")
            }
        }
    }

    private fun getCityName(lat: Double,long: Double):String{
        var cityName:String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:","Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }



}