package mx.tec.nuevoamigo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URLEncoder
import java.util.*


enum class ProviderType{
    GOOGLE,
    FACEBOOK
}



class MainActivity : AppCompatActivity() {
    val PERMISSION_ID = 1010
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    var ubicacionUser:String = ""

        private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.AppTheme_NoActionBar)
        //android:theme="@style/Theme.AppCompat.NoActionBar"
        var user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // Name, email address, and profile photo Url

            val i = Intent(this@MainActivity, MainPage::class.java)
            i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        /*Hash code*/ /*lo corres charls*/ /*Descomenta esto mi pana*/
        /*
        try {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val messageDigest =
                    MessageDigest.getInstance("SHA")
                messageDigest.update(signature.toByteArray())
                Log.d(
                    "KeyHash:",
                    Base64.encodeToString(
                        messageDigest.digest(),
                        Base64.DEFAULT
                    )
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }*/


        RequestPermission()
        getLastLocation()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //verficaSession()


        //WHATSAPP INTENT

        val btnWhatsapp= findViewById<Button>(R.id.btnWhatsapp)
        btnWhatsapp.setOnClickListener {

            val contact = "+52 7442667914" // use country code with your phone number

            val url = "https://api.whatsapp.com/send?phone=$contact"+"&text="+ URLEncoder.encode("hola estoy interesado por el perrito", "UTF-8");

       val intent = Intent()
            intent.type = "text/plain"


            intent.setPackage("com.whatsapp")
            intent.setData(Uri.parse(url))

            intent.putExtra(Intent.EXTRA_TEXT, "Enviando mensaje")
            //startActivity(intent);
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                var builder = AlertDialog.Builder(this)
                builder.setTitle("Whatsapp no instalado")
                builder.setMessage("Asegúrate de tener instalada la aplicación de whatsapp, para ejecutar está función.")
                builder.setPositiveButton("ENTENDIDO",
                    { dialogInterface: DialogInterface, i: Int -> })
                builder.show()
            }

        }

        btnGoogle.setOnClickListener {


            val googleConf= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
                getString(
                    R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)


        }

        //LINEA DE PRUEBA
        val db = FirebaseFirestore.getInstance()
        imgLogo.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {


                    override fun onSuccess(result: LoginResult?) {
                        result?.let {
                            val token = it.accessToken
                            val credential = FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        db.collection("Persona").document(it.result!!.user!!.uid)
                                            .get()
                                            .addOnSuccessListener { document ->
                                                if (document.data == null) {
                                                    Log.d(
                                                        "Persona NO registrada",
                                                        "DocumentSnapshot data: ${document!!.data}"
                                                    )
                                                    var i = Intent(
                                                        this@MainActivity,
                                                        edit_perfil::class.java
                                                    )
                                                    i.putExtra(
                                                        "name",
                                                        it.result?.user?.displayName ?: ""
                                                    )
                                                    i.putExtra(
                                                        "Ubicacion",
                                                        ubicacionUser
                                                    )
                                                    i.flags =
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                    startActivity(i)
                                                } else {
                                                    Log.d(
                                                        "Persona ya registrada",
                                                        "DocumentSnapshot data: ${document!!.data}"
                                                    )
                                                    var i = Intent(
                                                        this@MainActivity,
                                                        MainPage::class.java
                                                    )
                                                    i.putExtra(
                                                        "Ubicacion",
                                                        ubicacionUser
                                                    )
                                                    i.flags =
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                    startActivity(i)
                                                }
                                            }
                                    }
                                }
                        }
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {

                        alertError();

                    }

                })
        }
        db.collection("Persona")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d("TEST",
                            document.id + " => " + document.data
                        )
                    }
                } else {
                    Log.w("TEST ERROR", "Error getting documents.", task.exception)

                }
            }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode==GOOGLE_SIGN_IN){

            val task =GoogleSignIn.getSignedInAccountFromIntent(data)

            try{

                val db = FirebaseFirestore.getInstance() //linea codigo repetida
                val account = task.getResult(ApiException::class.java)
                if(account!=null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                        if (it.isSuccessful) {
                            db.collection("Persona").document(it.result!!.user!!.uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.data == null) {
                                        Log.d(
                                            "Persona NO registrada",
                                            "DocumentSnapshot data: ${document!!.data}"
                                        )
                                        var i = Intent(
                                            this@MainActivity,
                                            edit_perfil::class.java
                                        )
                                        i.putExtra(
                                            "name",
                                            it.result?.user?.displayName ?: ""
                                        )
                                        i.putExtra(
                                            "Ubicacion",
                                            ubicacionUser
                                        )
                                        i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(i)
                                    } else {
                                        Log.d(
                                            "Persona ya registrada",
                                            "DocumentSnapshot data: ${document!!.data}"
                                        )
                                        var i = Intent(
                                            this@MainActivity,
                                            MainPage::class.java
                                        )
                                        i.putExtra(
                                            "Ubicacion",
                                            ubicacionUser
                                        )
                                        i.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(i)
                                    }
                                }
                        }
                    }

                }


            }catch (e: ApiException){

            }


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
                    var location: Location? = task.result
                    if(location == null){
                        NewLocationData()
                    }else{
                        Log.d("Debug:", "Your Location:" + location.longitude)
                        ubicacionUser = getCityName(location.latitude, location.longitude)
                        Log.d("Debug:", ubicacionUser)
                    }
                }
            }else{
                Toast.makeText(this, "Please Turn on Your device Location", Toast.LENGTH_SHORT).show()
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
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private fun alertError(){

        var builder = AlertDialog.Builder(this)
        builder.setTitle("Oops algo ocurrió")
        builder.setMessage("Ocurrió un problema, por favor intenta más tarde.")
        builder.setPositiveButton("ENTENDIDO", { dialogInterface: DialogInterface, i: Int -> })
        builder.show()

    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:", "your last last location: " + lastLocation.longitude.toString())
            //textView.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n" + getCityName(lastLocation.latitude,lastLocation.longitude)
        }
    }

    private fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }

        return false

    }

    fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
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
                Log.d("Debug:", "You have the Permission")
            }
        }
    }

    private fun getCityName(lat: Double, long: Double):String{
        var cityName:String = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }



}

