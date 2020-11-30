package mx.tec.nuevoamigo

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
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
import mx.tec.nuevoamigo.utils.Credentials
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

            val i = Intent(this@MainActivity,MainPage::class.java )
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //verficaSession()


        //WHATSAPP INTENT



        btnGoogle.setOnClickListener {


            val googleConf= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)


        }

        //LINEA DE PRUEBA
        val db = FirebaseFirestore.getInstance()
        btnFacebook.setOnClickListener {
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
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
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


            }catch (e:ApiException){

            }


        }

    }
    private fun alertError(){

        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.oops_algo_ocurrio))
        builder.setMessage(getString(R.string.Ocurrio_un_problema_por_favor_intenta_mas_tarde))
        builder.setPositiveButton(getString(R.string.entendido), { dialogInterface: DialogInterface, i: Int -> })
        builder.show()

    }


}

