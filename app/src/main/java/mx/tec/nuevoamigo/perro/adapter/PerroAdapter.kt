package mx.tec.nuevoamigo.perro.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import mx.tec.nuevoamigo.R
import mx.tec.nuevoamigo.perro.model.Perro

class PerroAdapter(private val context : Context, private val layout: Int, private val dataSouce: List<Perro>) : BaseAdapter() {
    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var storage: FirebaseStorage
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.layout_elemento_perro, parent, false)
        //hacer referencia a los widgets
        //Se usa la referencia de la vista que desclaramos para buscar ahi los elementos
        storage = Firebase.storage
        val imgImagen = view.findViewById<ImageView>(R.id.imgPerro)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtEstado = view.findViewById<TextView>(R.id.txtEstado)
        //Llenar los atributos de valor de los widgets
        val elemento = dataSouce[position]

        var picassoInstance = Picasso.Builder(context)
            .addRequestHandler(FirebaseRequestHandler())
            .build()

        val imageRefP = storage.getReferenceFromUrl("${elemento.imagen}")
        picassoInstance.load("$imageRefP").placeholder(R.drawable.cargando_blanco).error(R.drawable.avatar).into(imgImagen)

        txtNombre.text = elemento.nombre
        txtEstado.text = elemento.estado
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