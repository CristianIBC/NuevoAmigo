package mx.tec.nuevoamigo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_registrar_perrita.*
import mx.tec.nuevoamigo.perro.model.PerroP
import java.io.ByteArrayOutputStream
import java.io.IOException

class RegistrarPerrita : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var filePathP: Uri? = null
    private var filePathF: Uri? = null

    private var boton: Int = 0
    var bitmapP: Bitmap? = null
    var bitmap: Bitmap? = null
    lateinit var storage: FirebaseStorage
    var storageRef: StorageReference? = null
    var time: Long = 0
    var idPersona:String = ""
    val db = FirebaseFirestore.getInstance()
    var ciudadPerrito: String = ""
    var sexo: String? = ""
    var recurso = "gs://nuevo-amigo.appspot.com/imagenesPerro/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_perrita)
        storage = Firebase.storage
        storageRef = storage.reference
        //ciudad del usuario para registrar a la perrrita
        val ciudadActual = intent.getStringExtra("ciudadActual")
        var ciudadUsuario: String = ""

        //para el id de la persona
        idPersona = intent.getStringExtra("idPersona").toString()
        //Recuperar ciudad del usuario
        db.collection("Persona").document(idPersona!!).get().addOnSuccessListener {document->
            ciudadUsuario= document.data!!["Ciudad"].toString()
        }
        //spiner
        var spinnerItemSelected = 0
        val spnrTamaño = findViewById<Spinner>(R.id.spnrTamaño)

        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTamaño,

            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnrTamaño.adapter = adapter

        }



        btnRegistrar.setOnClickListener {
            time = System.currentTimeMillis().toLong()
            recurso += time
            sexo = when(radioGroup.checkedRadioButtonId){
                R.id.rGHembraR -> "Hembra"
                R.id.rGMachoR -> "Macho"
                else -> ""
            }
            if(edtNombreR.text.toString()!="" && spnrTamaño.selectedItemId != 0.toLong() && edtRazaR.text.toString()!="" && edtDescripcionR.text.toString()!="" && edtEdadR.text.toString()!="" && sexo!="" && bitmapP!=null && bitmap!=null){
                Log.d("test", "tamaño: " + spnrTamaño.selectedItem.toString())
                if(ciudadActual == ciudadUsuario){
                    ciudadPerrito = ciudadUsuario
                }else{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Alerta")
                        .setMessage("¡Desea registrarlo/a con la ciudad que tiene registrada en su perfil o la ciudad en donde está?")

                        .setPositiveButton(ciudadUsuario){ dialog, button ->
                            ciudadPerrito = ciudadUsuario
                            guardarPerrito()
                        }
                        .setNegativeButton(ciudadActual){ dialog, button ->
                            ciudadPerrito = ciudadActual!!
                            guardarPerrito()
                        }
                        .show()
                }

            }else{
                Toast.makeText(this,
                    "No puedes dejar campos en blanco (incluidas imagenes)",
                    Toast.LENGTH_LONG).show()
            }
        }

        btnFotoPR.setOnClickListener {
            boton = 1
            Log.d("test", "previo: ${boton}")
            launchGallery()
            //boton = 0
        }
        btnFotoR.setOnClickListener {
            boton=2
            launchGallery()
            //boton=0
        }
    }
    private fun guardarPerrito(){
        uploadImage()
        var perrito = PerroP(edtNombreR.text.toString(), edtDescripcionR.text.toString(),
            "Disponible", idPersona!!, recurso + "F.jpeg",
            recurso + "P.jpeg", edtRazaR.text.toString(),
            sexo!!, spnrTamaño.selectedItem.toString(), edtEdadR.text.toString().toLong(),time, ciudadPerrito)
        db.collection("Perrito").add(perrito.convTomap()).addOnSuccessListener {
            Log.d("testU","perrito Ingresado")
            var i = Intent(this@RegistrarPerrita, CatalogoPropio::class.java)
            i.flags= Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }
    private fun uploadImage() {
        var baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var data = baos.toByteArray()
        var mountainsRef = storageRef?.child("imagenesPerro/" + time + "F.jpeg")
        var uploadTask = mountainsRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            Log.d("test", "error")
        }?.addOnSuccessListener { taskSnapshot ->
            Log.d("test", taskSnapshot.toString())
        }
        baos = ByteArrayOutputStream()
        bitmapP?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        data = baos.toByteArray()
        mountainsRef = storageRef?.child("imagenesPerro/" + time + "P.jpeg")
        uploadTask = mountainsRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            Log.d("test", "error")
        }?.addOnSuccessListener { taskSnapshot ->
            Log.d("test", taskSnapshot.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            Log.d("test", boton.toString())
            if (boton==1){
                try {
                    bitmapP = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Log.d("test", bitmapP.toString())
                    imgPR.setImageBitmap(bitmapP)
                    filePathP=filePath
                    Log.d("test", "filepath: $filePathP")
                    //uploadImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if(boton==2){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Log.d("test", bitmap.toString())
                    imgR.setImageBitmap(bitmap)
                    filePathF=filePath
                    //uploadImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            boton=0
            filePath = null
            Toast.makeText(this, "Foto agregada", Toast.LENGTH_SHORT).show()
        }
    }
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

}