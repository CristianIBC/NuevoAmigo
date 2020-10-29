package mx.tec.nuevoamigo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.Api
import kotlinx.android.synthetic.main.activity_main.*


enum class ProviderType{
    GOOGLE,
    FACEBOOK
}



class MainActivity : AppCompatActivity() {
        private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //verficaSession()

        btnGoogle.setOnClickListener {


            val googleConf= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)

          //  var i = Intent(this@MainActivity, edit_perfil::class.java)
         //   startActivity(i)
        }

        //LINEA DE PRUEBA
        val db = FirebaseFirestore.getInstance()
        imgLogo.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
            object: FacebookCallback<LoginResult>{


                override fun onSuccess(result: LoginResult?) {
                    result?.let{
                        val token = it.accessToken
                        val credential = FacebookAuthProvider.getCredential(token.token)
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                            if(it.isSuccessful){
                                db.collection("Persona").document(it.result!!.user!!.uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document.data == null) {
                                            Log.d("Persona NO registrada", "DocumentSnapshot data: ${document!!.data}")
                                            var i = Intent(this@MainActivity, edit_perfil::class.java)

                                            i.putExtra("name",it.result?.user?.displayName ?: "")
                                            startActivity(i)
                                        } else {
                                            Log.d("Persona ya registrada", "DocumentSnapshot data: ${document!!.data}")
                                            var i = Intent(this@MainActivity, MainPage::class.java)
                                            val sharepref =getSharedPreferences(getString(R.string.archivoSesion), Context.MODE_PRIVATE)
                                            with(sharepref.edit()){
                                                putString("email", it!!.result!!.user!!.email)
                                                putString("user", it!!.result!!.user!!.displayName)
                                                commit()
                                            }
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

    private fun verficaSession(){

        val sharepref = getSharedPreferences(getString(R.string.archivoSesion),Context.MODE_PRIVATE)
        val email = sharepref.getString("email",null)
        val user = sharepref.getString("user",null)
         if(email!=null && user!=null){


         }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //callbackManager.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GOOGLE_SIGN_IN){

            val task =GoogleSignIn.getSignedInAccountFromIntent(data)


                val account = task.getResult(ApiException::class.java)
                if(account!=null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                        if(it.isSuccessful){
                            //intent

                            var i = Intent(this@MainActivity, edit_perfil::class.java)
                         i.putExtra("name", it!!.result!!.user!!.displayName)
                            startActivity(i)

                        }else{

                            //alert
                        }
                    }

                }

        }

    }

}