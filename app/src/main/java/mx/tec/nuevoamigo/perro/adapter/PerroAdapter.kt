package mx.tec.nuevoamigo.perro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import mx.tec.nuevoamigo.R
import mx.tec.nuevoamigo.perro.model.Perro

class PerroAdapter(private val context : Context, private val layout: Int, private val dataSouce: List<Perro>) : BaseAdapter() {
    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.layout_elemento_perro, parent, false)
        //hacer referencia a los widgets
        //Se usa la referencia de la vista que desclaramos para buscar ahi los elementos

        val imgImagen = view.findViewById<ImageView>(R.id.imgPerro)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtEstado = view.findViewById<TextView>(R.id.txtEstado)
        //Llenar los atributos de valor de los widgets
        val elemento = dataSouce[position]

        txtNombre.text = elemento.nombre
        txtEstado.text = elemento.estado
        imgImagen.setImageResource(elemento.imagen)

        return view
    }

    override fun getItem(position: Int): Any {
        return dataSouce[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSouce.size
    }
}