package mx.tec.nuevoamigo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.main_fragment.*
import mx.tec.nuevoamigo.perro.adapter.PerroMainAdapter
import mx.tec.nuevoamigo.perro.adapter.RecyclerViewClickInterface
import mx.tec.nuevoamigo.perro.model.PerroMain
import java.util.*


class MainPage : AppCompatActivity() , RecyclerViewClickInterface {
    //gps
    val PERMISSION_ID = 1010
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    //gps

    var datos= mutableListOf<PerroMain>()
    var ubicActual:String = ""
    val db = FirebaseFirestore.getInstance()


    var user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_fragment)
        val fragmentManager = supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        ft.add(R.id.fragment,ListaPerros())
        ft.commit()
        btoomNavigation.setOnNavigationItemSelectedListener{item ->
            when (item.itemId) {
                R.id.buscarPerros -> {
                    if(btoomNavigation.selectedItemId != R.id.buscarPerros) {
                        supportActionBar!!.title = getString(R.string.amigos_caninos_en_tu_ciudad)
                        val ft = fragmentManager.beginTransaction()
                        ft.replace(R.id.fragment, ListaPerros(), "0")
                        ft.commit()
                    }
                }
                R.id.tuPefil -> {
                    if(btoomNavigation.selectedItemId != R.id.tuPefil) {
                        supportActionBar!!.title = getString(R.string.tu_perfil)
                        val ft = fragmentManager.beginTransaction()
                        ft.replace(R.id.fragment, PerfilUsuario(), "1")
                        ft.commit()
                    }
                }
                R.id.tusPerros -> {
                    if(btoomNavigation.selectedItemId != R.id.tusPerros) {
                        supportActionBar!!.title = getString(R.string.tu_catalogo_de_perros)
                        val ft = fragmentManager.beginTransaction()
                        ft.replace(R.id.fragment, CatalogoPropio(), "2")
                        ft.commit()
                    }
                }
                else -> {
                    print("algo")
                }
            }
            true
        }
    }

    override fun onItemClick(position: Int) {
        var i = Intent(this@MainPage, InfoPerritoOtro::class.java)
        i.putExtra("idPerrito", datos[position].id)
        startActivity(i)
    }

    override fun onLongItemClick(position: Int) {
        TODO("Not yet implemented")
    }



}




