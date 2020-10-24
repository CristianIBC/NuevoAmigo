package mx.tec.nuevoamigo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //LINEA DE PRUEBA
        val db = FirebaseFirestore.getInstance()
        imgLogo.setOnClickListener {
            var i = Intent(this@MainActivity, edit_perfil::class.java)
            startActivity(i)
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
}