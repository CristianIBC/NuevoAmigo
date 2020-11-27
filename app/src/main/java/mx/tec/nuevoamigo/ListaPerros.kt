package mx.tec.nuevoamigo

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.Image
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.activity_main_page.view.*
import mx.tec.nuevoamigo.perro.adapter.PerroMainAdapter
import mx.tec.nuevoamigo.perro.adapter.RecyclerViewClickInterface
import mx.tec.nuevoamigo.perro.model.PerroMain
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListaPerros.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListaPerros : Fragment() , RecyclerViewClickInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    //gps
    val PERMISSION_ID = 1010
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    //gps

    var datos= mutableListOf<PerroMain>()
    var ubicActual:String = ""
    val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var spinnerOpciones: Spinner?= null
    var spinnerOpciones2: Spinner?= null
    var spinnerOpciones3: Spinner?= null

    var ciudadesDisponibles: ArrayList<String> = arrayListOf("Opciones...")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //  Thread.sleep(1000)
        requireActivity().setTheme(R.style.AppTheme)
        //gps
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        RequestPermission()
        getLastLocation()

        //--
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.activity_main_page, container, false)
        spinnerOpciones3 = view.findViewById<Spinner>(R.id.spinnerCiudadMain)


        var spinner1Selected = 0
        spinnerOpciones = view.findViewById<Spinner>(R.id.spinnerHeight)

        ArrayAdapter.createFromResource(requireContext(),
            R.array.opcionesTamaño,
            android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOpciones!!.adapter = adapter
        }


        spinnerOpciones!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinner1Selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        var spinner3Selected = 0
        spinnerOpciones3!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinner3Selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        var spinner2Selected = 0
        spinnerOpciones2 = view.findViewById<Spinner>(R.id.spinnerSex)

        ArrayAdapter.createFromResource(requireContext(),
            R.array.opcionesSexo,
            android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOpciones2!!.adapter = adapter
        }
        /*val btnTusPerros = view.findViewById<ImageButton>(R.id.btnTusPerros)
        btnTusPerros.setOnClickListener {
            var i = Intent(requireContext(), CatalogoPropio::class.java)
            i.putExtra("ciudadActual", ubicActual)
            startActivity(i)
        }*/
        spinnerOpciones2!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinner2Selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        /*val imgPersonaMain = view.findViewById<ImageView>(R.id.imgPersonaMain)
        imgPersonaMain.setOnClickListener {
            var i = Intent(requireContext(), PerfilUsuario::class.java)
            startActivity(i)
        }*/

        var fotoUser:String?=null

        if (user != null) {
            fotoUser = user!!.photoUrl.toString()
           // Picasso.get().load("$fotoUser?type=large").into(imgPersonaMain)
        }

        //BOTON DEL FILTRADO
        val btnFiltrar = view.findViewById<Button>(R.id.btnFiltrar)
        btnFiltrar.setOnClickListener {
            cargarPerros()
        }

        return view
    }
    private fun cargarPerros(){
        datos.clear()
        var perros: Query? = null

        if(spinnerOpciones!!.selectedItem.toString() != "Opciones..." && spinnerOpciones2!!.selectedItem.toString() == "Opciones..." && spinnerOpciones3!!.selectedItem.toString() == "Opciones...")
        {
            perros = db.collection("Perrito")
                .whereEqualTo("ciudad", ubicActual)
                .whereEqualTo("estado", "Disponible")
                .whereNotEqualTo("idPersona", user?.uid.toString())
                .whereEqualTo("tamaño", spinnerOpciones!!.selectedItem.toString())

        }else if(spinnerOpciones!!.selectedItem.toString() == "Opciones..." && spinnerOpciones2!!.selectedItem.toString() != "Opciones..." && spinnerOpciones3!!.selectedItem.toString() == "Opciones...")
        {
            perros = db.collection("Perrito")
                .whereEqualTo("ciudad", ubicActual)
                .whereEqualTo("estado", "Disponible")
                .whereNotEqualTo("idPersona", user?.uid.toString())
                .whereEqualTo("sexo", spinnerOpciones!!.selectedItem.toString())

        }else if(spinnerOpciones!!.selectedItem.toString() == "Opciones..." && spinnerOpciones2!!.selectedItem.toString() == "Opciones..." && spinnerOpciones3!!.selectedItem.toString() != "Opciones...")
        {
            perros = db.collection("Perrito")
                .whereEqualTo("ciudad", spinnerOpciones3!!.selectedItem.toString())
                .whereEqualTo("estado", "Disponible")
                .whereNotEqualTo("idPersona", user?.uid.toString())

        }else if (spinnerOpciones!!.selectedItem.toString() != "Opciones..." && spinnerOpciones2!!.selectedItem.toString() != "Opciones..." && spinnerOpciones3!!.selectedItem.toString() == "Opciones...")
        {
            perros = db.collection("Perrito")
                .whereEqualTo("ciudad", ubicActual)
                .whereEqualTo("estado", "Disponible")
                .whereNotEqualTo("idPersona", user?.uid.toString())
                .whereEqualTo("tamaño", spinnerOpciones!!.selectedItem.toString())
                .whereEqualTo("sexo", spinnerOpciones2!!.selectedItem.toString())
        }else if(spinnerOpciones!!.selectedItem.toString() != "Opciones..." && spinnerOpciones2!!.selectedItem.toString() == "Opciones..." && spinnerOpciones3!!.selectedItem.toString() != "Opciones...")
        {
            perros = db.collection("Perrito")
                .whereEqualTo("ciudad", spinnerOpciones3!!.selectedItem.toString())
                .whereEqualTo("estado", "Disponible")
                .whereNotEqualTo("idPersona", user?.uid.toString())
                .whereEqualTo("tamaño", spinnerOpciones!!.selectedItem.toString())
        }else if(spinnerOpciones!!.selectedItem.toString() == "Opciones..." && spinnerOpciones2!!.selectedItem.toString() != "Opciones..." && spinnerOpciones3!!.selectedItem.toString() != "Opciones...")
        {
            perros = db.collection("Perrito")
                .whereEqualTo("ciudad", spinnerOpciones3!!.selectedItem.toString())
                .whereEqualTo("estado", "Disponible")
                .whereNotEqualTo("idPersona", user?.uid.toString())
                .whereEqualTo("sexo", spinnerOpciones2!!.selectedItem.toString())
        }else if(spinnerOpciones!!.selectedItem.toString() != "Opciones..." && spinnerOpciones2!!.selectedItem.toString() != "Opciones..." && spinnerOpciones3!!.selectedItem.toString() != "Opciones...")
        {
            perros = db.collection("Perrito")
                .whereEqualTo("ciudad", spinnerOpciones3!!.selectedItem.toString())
                .whereEqualTo("estado", "Disponible")
                .whereNotEqualTo("idPersona", user?.uid.toString())
                .whereEqualTo("tamaño", spinnerOpciones!!.selectedItem.toString())
                .whereEqualTo("sexo", spinnerOpciones2!!.selectedItem.toString())
        }else
        {
            perros = db.collection("Perrito")
                .whereEqualTo("ciudad", ubicActual)
                .whereEqualTo("estado", "Disponible")
                .whereNotEqualTo("idPersona", user?.uid.toString())
        }
        perros.get().addOnSuccessListener { documents ->
            for (document in documents) {
                Log.e("Hola", "entre")
                datos.add(PerroMain(document.id,
                    document.data!!["nombre"].toString(),
                    document.data!!["raza"].toString(),
                    document.data!!["edad"].toString(),
                    document.data!!["sexo"].toString(),
                    document.data!!["imagenPerfil"].toString()))
            }
            datos.forEach{
                Log.d("Perro", it.nombre)
            }
            if (datos.size == 0){
                Toast.makeText(requireContext(),
                    "No hay perritos para mostrar",
                    Toast.LENGTH_LONG).show()
            }
            val elementoAdapter = PerroMainAdapter(requireContext(),
                R.layout.act_recycler,
                datos,
                this)
            if(rvLista != null){
                rvLista.layoutManager = GridLayoutManager(requireContext(),
                    1,
                    GridLayoutManager.VERTICAL,
                    false)
                rvLista.setHasFixedSize(true)
                rvLista.adapter= elementoAdapter
            }

        }
    }
    override fun onItemClick(position: Int) {
        var i = Intent(requireContext(), InfoPerritoOtro::class.java)
        i.putExtra("idPerrito", datos[position].id)
        startActivity(i)
    }

    override fun onLongItemClick(position: Int) {
        TODO("Not yet implemented")
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnRefresh->{
                spinnerOpciones!!.setSelection(0)
                spinnerOpciones2!!.setSelection(0)
                spinnerOpciones3!!.setSelection(0)
                cargarPerros()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //--------------------------------------------------GPS

    fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
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
                        Log.d("Debug:", "Your Location:" + location.longitude)
                        ubicActual = getCityName(location.latitude, location.longitude)
                        Log.d("AAAAAAA", ubicActual)
                        //Busqueda de las ciudades donde hay perros
                        db.collection("Perrito").whereEqualTo("estado", "Disponible").get().addOnSuccessListener { documents->
                            documents.forEach{
                                ciudadesDisponibles.add(it.data["ciudad"].toString())
                            }
                            var adapterCiudad = ArrayAdapter(requireContext(),
                                android.R.layout.simple_spinner_item,
                                ciudadesDisponibles.distinct())
                            adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerOpciones3!!.adapter= adapterCiudad
                            cargarPerros()
                            var bundle= Bundle()
                            bundle.putString("ciudadActual", ubicActual)
                            parentFragmentManager.setFragmentResult("key", bundle)
                        }


                    }
                }
            }else{
                Toast.makeText(requireContext(), "Prenda su GPS y reinicie la aplicación ", Toast.LENGTH_SHORT).show()
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
            locationRequest, locationCallback, Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:", "your last last location: " + lastLocation.longitude.toString())
        }
    }

    private fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
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
                Log.d("Debug:", "You have the Permission")
            }
        }
    }

    private fun getCityName(lat: Double, long: Double):String{
        var cityName:String = ""
        var countryName = ""
        var geoCoder = Geocoder(requireContext(), Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }

}