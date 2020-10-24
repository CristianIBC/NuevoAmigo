package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_registrar_perrita.*

class RegistrarPerrita : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_perrita)

        btnRegistrar.setOnClickListener {
            var i = Intent(this, CatalogoPropio::class.java)
            startActivity(i)
        }
    }
}