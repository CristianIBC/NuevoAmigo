package mx.tec.nuevoamigo.perro.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import mx.tec.nuevoamigo.R
import mx.tec.nuevoamigo.perro.model.PerroMain

class PerroMainAdapter(private val context : Context,
                       private val layout: Int,
                       private val dataSource: MutableList<PerroMain>,
                       private val clickInterface: RecyclerViewClickInterface) : RecyclerView.Adapter<PerroMainAdapter.ElementoViewHolder>() {
    class ElementoViewHolder(context: Context,
                             inflater: LayoutInflater,
                             parent: ViewGroup,
                             layout: Int,
                             clickInterface: RecyclerViewClickInterface):
        RecyclerView.ViewHolder(inflater.inflate(layout, parent, false)){
        lateinit var storage: FirebaseStorage
        //ViewHolder es la clase que se encarga de MANIPULAR los controles del elemento
        var imagen: ImageView? = null
        var nombrePerro: TextView? = null
        var edadPerro: TextView? = null
        var razaPerro: TextView? = null
        var sexoPerro: TextView? = null

        var picassoInstance = Picasso.Builder(context)
            .addRequestHandler(FirebaseRequestHandler())
            .build()

        init{
            itemView.setOnClickListener(View.OnClickListener {
                clickInterface.onItemClick(adapterPosition)
            })
            itemView.setOnLongClickListener(View.OnLongClickListener {
                clickInterface.onLongItemClick(adapterPosition)
                return@OnLongClickListener true
            })
            storage = Firebase.storage
            imagen = itemView.findViewById(R.id.imgPerrito)
            nombrePerro = itemView.findViewById(R.id.txtNombrePerrito)
            edadPerro = itemView.findViewById(R.id.txtEdadPerro)
            razaPerro = itemView.findViewById(R.id.txtRazaPerro)
            sexoPerro = itemView.findViewById(R.id.txtSexoPerro)

        }

        fun bindData(perroMain: PerroMain){
            //imagenes

            val imageRefP = storage.getReferenceFromUrl("${perroMain.imagen}")
            picassoInstance.load("$imageRefP").into(imagen)


            val gsReferencePerfil = storage.getReferenceFromUrl("${perroMain.imagen}")
            val ONE_MEGABYTE: Long = 1024*1024
            gsReferencePerfil.getBytes(ONE_MEGABYTE*10).addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it,0, it.size)
                imagen?.setImageBitmap(bmp)
            }
            //end imagenes

            //imagen!!.setImageResource(perroMain.imagen)
            nombrePerro!!.text = perroMain.nombre
            edadPerro!!.text = perroMain.edad + " meses"
            razaPerro!!.text = perroMain.raza
            sexoPerro!!.text = perroMain.sexo

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ElementoViewHolder(context,inflater, parent, layout, clickInterface)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: ElementoViewHolder, position: Int) {
        val elemento = dataSource[position]
        holder.bindData(elemento)
    }
    fun clear() {
        val size: Int = dataSource.size
        dataSource.clear()
        notifyItemRangeRemoved(0, size)
    }
}