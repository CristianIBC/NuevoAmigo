package mx.tec.nuevoamigo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class edit_perfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_perfil)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}