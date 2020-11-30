package mx.tec.nuevoamigo

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.ContentProviderClient
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.facebook.places.internal.LocationPackageRequestParams
import com.google.android.gms.location.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_perfil.*
import kotlinx.android.synthetic.main.activity_perfil_usuario.*
import java.util.*
import java.util.jar.Manifest
import kotlin.time.hours


class edit_perfil : AppCompatActivity() {


    //------------------------variables gps
    val PERMISSION_ID = 1010
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val c= Calendar.getInstance().time
    var hour= c.hours
    var minute= c.minutes
    var emailUser: String = ""
    var uid: String = ""
    var photoUser: String? = null
    var isAlbergue: Boolean = false
    var nameUser: String =""
    val db = FirebaseFirestore.getInstance()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        //-------------------------GPS MIS PANAS
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)



        //----------------------------
        RequestPermission()
        getLastLocation()
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Name, email address, and profile photo Url
            emailUser = user.email!!
            nameUser = user.displayName!!
            uid = user.uid!!
            photoUser = user.photoUrl.toString()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_perfil)

        var name= intent.getStringExtra("name")
        txtEmail.text =name
        Picasso.get().load("$photoUser?type=large").into(imgPerfil)
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
            user["Ciudad"] = txtDireccion.text.toString()
            user["Email"] = emailUser
            user["IsAlbergue"] = isAlbergue
            user["Telefono"] = txtCelular.text.toString()
            user["HorarioAtencion"] = txtHoraInicio.text.toString() +"-"+ txtHoraFin.text.toString()
            db.collection("Persona").document(uid)
                .set(user)
                .addOnSuccessListener {


                    Log.d("Persona registrada", "DocumentSnapshot successfully written!")
                    }
                .addOnFailureListener(OnFailureListener { e ->
                    Log.w(
                        "Error al guardar",
                        "Error adding document",
                        e
                    )
                })
            var i = Intent(this@edit_perfil, MainPage::class.java)
            i.putExtra("Ubicacion", txtDireccion.text.toString())
            startActivity(i)
        }



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
                    var location:Location? = task.result
                    if(location == null){
                        NewLocationData()
                    }else{
                        Log.d("Debug:" ,"Your Location:"+ location.longitude)
                        txtDireccion.setText(getCityName(location.latitude,location.longitude))
                    }
                }
            }else{
                Toast.makeText(this,getString(R.string.GPS_peticion),Toast.LENGTH_SHORT).show()
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
            locationRequest,locationCallback,Looper.myLooper()
        )
    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
            textView.text = getString(R.string.You_Last_Location_is_Long)+ lastLocation.longitude + getString(R.string.lat) + lastLocation.latitude + "\n" + getCityName(lastLocation.latitude,lastLocation.longitude)
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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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

    fun onclick(view: View) {
        if(view.id == R.id.switchAlbergue){
            if(switchAlbergue.isChecked){
                isAlbergue = true
            }
        }
    }


}