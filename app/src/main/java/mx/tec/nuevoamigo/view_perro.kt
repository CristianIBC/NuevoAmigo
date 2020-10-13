package mx.tec.nuevoamigo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

class view_perro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_perro)

        var spinner1Selected = 0
        val spinnerOpciones = findViewById<Spinner>(R.id.spinnerSexoPerro)

        ArrayAdapter.createFromResource(this,R.array.opcionesSexo,android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOpciones.adapter = adapter
        }

        spinnerOpciones.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinner1Selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        var spinner2Selected = 0
        val spinnerOpciones2 = findViewById<Spinner>(R.id.spinnerTamanoPerro)

        ArrayAdapter.createFromResource(this,R.array.opcionesTamaño,android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOpciones2.adapter = adapter
        }

        spinnerOpciones2.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinner2Selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        var spinner3Selected = 0
        val spinnerOpciones3 = findViewById<Spinner>(R.id.spinnerEstadoPerro)

        ArrayAdapter.createFromResource(this,R.array.opcionesEstado,android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerOpciones3.adapter = adapter
        }

        spinnerOpciones3.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinner3Selected = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

    }
}