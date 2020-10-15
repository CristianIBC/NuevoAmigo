package mx.tec.nuevoamigo

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker
import kotlinx.android.synthetic.main.activity_edit_perfil.*
import java.util.*

class edit_perfil : AppCompatActivity() {

    var hour=0
    var minute=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_perfil)

        btnHorarioInicio.setOnClickListener{

         TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->  },hour,minute,true).show()
        }

        btnHorarioFin.setOnClickListener {
           TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> }, hour, minute, true).show()
        }
        btnGuardarPerfil.setOnClickListener {
            var i = Intent(this@edit_perfil, MainPage::class.java)
            startActivity(i)
        }

    }

    private fun getTime(){

        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        minute =cal.get(Calendar.MINUTE)
    }

    private fun pickTime(){

    }

}