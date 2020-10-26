package mx.tec.nuevoamigo

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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


enum class ProviderType{
    GOOGLE,
    FACEBOOK
}



class MainActivity : AppCompatActivity() {

    private val callbackManager = CallbackManager.Factory.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                                var i = Intent(this@MainActivity, edit_perfil::class.java)
                                i.putExtra("name",it.result?.user?.displayName ?: "")
                                startActivity(i)
                            }
                        }
                    }
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }

            })


            //var i = Intent(this@MainActivity, edit_perfil::class.java)
            //startActivity(i)
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
        callbackManager.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}