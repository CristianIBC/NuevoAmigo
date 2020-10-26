package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //LINEA DE PRUEBA
        imgLogo.setOnClickListener {
            var i = Intent(this@MainActivity, edit_perfil::class.java)
            startActivity(i)
        }
    }
}