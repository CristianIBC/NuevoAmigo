package mx.tec.nuevoamigo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_catalogo.*
import kotlinx.android.synthetic.main.activity_catalogo.listaPerro
import kotlinx.android.synthetic.main.activity_catalogo_propio.*
import kotlinx.android.synthetic.main.activity_main_page.*
import mx.tec.nuevoamigo.perro.adapter.PerroAdapter
import mx.tec.nuevoamigo.perro.adapter.PerroMainAdapter
import mx.tec.nuevoamigo.perro.model.Perro
import mx.tec.nuevoamigo.perro.model.PerroMain

class CatalogoPropio : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // parentFragmentManager.setFragmentResultListener()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_catalogo_propio, container, false)

        var ciudadActual = ""//requireActivity().intent.getStringExtra("ciudadActual")
        parentFragmentManager.setFragmentResultListener("key", this, FragmentResultListener { requestKey, result ->
            ciudadActual = result.getString("ciudadActual", "")
            Log.e("CATALOGO PROPIO", "ciudad Actual $ciudadActual")
        })
        val listaPerro = view.findViewById<ListView>(R.id.listaPerro)
        val btnMas = view.findViewById<Button>(R.id.btnMas)
        var user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var datos=  mutableListOf<Perro>()
        db.collection("Perrito").whereEqualTo("idPersona", user?.uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")

                    datos.add(Perro(document.id,document.data!!["nombre"].toString(), document.data!!["estado"].toString(), document.data!!["imagenPerfil"].toString()))
                    Log.d("TAG",datos.toString() )

                }
                val elementoAdapter = PerroAdapter(requireContext(), R.layout.layout_elemento_perro, datos)
                listaPerro.adapter= elementoAdapter
            }
        listaPerro.setOnItemClickListener { parent, view, position, id ->
            var i = Intent(requireContext(), InfoPerrita::class.java)
            i.putExtra("idPerro",datos[position].id)
            startActivity(i)
        }
        btnMas.setOnClickListener {
            var user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                var i = Intent(requireContext(), RegistrarPerrita::class.java)
                i.putExtra("idPersona", user.uid)
                i.putExtra("ciudadActual", ciudadActual!!)
                startActivity(i)
            }
        }
        return view
    }

}