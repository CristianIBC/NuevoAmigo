package mx.tec.nuevoamigo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_perrita)
        storage = Firebase.storage
        storageRef = storage.reference

        //spiner
        val datosSpinner = arrayListOf("Pequeño", "Mediano", "Grande", "Gigante")
        val adaptadorSpinner = ArrayAdapter(this, android.R.layout.simple_list_item_1, datosSpinner)
        spnrTamaño.adapter = adaptadorSpinner

        //esto se debe ir
        val idPersona = "5uy5A3MBF7Rc8RoDYW4h"

        val recurso = "gs://nuevo-amigo.appspot.com/imagenesPerro/"

        btnRegistrar.setOnClickListener {
            var sexo = when(radioGroup.checkedRadioButtonId){
                R.id.rGHembraR -> "Hembra"
                R.id.rGMachoR -> "Macho"
                else -> ""
            }
            if(edtNombreR.text.toString()!="" && edtRazaR.text.toString()!="" && edtDescripcionR.text.toString()!="" && edtEdadR.text.toString()!="" && sexo!="" && bitmapP!=null && bitmap!=null){
                Log.d("test", "tamaño: " + spnrTamaño.selectedItem.toString())
                uploadImage()
                var perrito = PerroP(edtNombreR.text.toString(), edtDescripcionR.text.toString(),
                    "Disponible", idPersona, recurso + edtNombreR.text.toString() + "F.jpeg",
                    recurso + edtNombreR.text.toString() + "P.jpeg", edtRazaR.text.toString(),
                    sexo, spnrTamaño.selectedItem.toString(), edtEdadR.text.toString().toLong())
                val db = FirebaseFirestore.getInstance()
                db.collection("Perrito").add(perrito.convTomap()).addOnSuccessListener {
                    Log.d("testU","perrito Ingresado")
                    TODO("mandar al activity necesario")
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

    private fun uploadImage() {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        var mountainsRef = storageRef?.child("imagenesPerro/" + edtNombreR.text.toString() + "F.jpeg")
        Log.d("test", "filepath upload ${filePathF}")
        var uploadTask = mountainsRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            Log.d("test", "error")
        }?.addOnSuccessListener { taskSnapshot ->
            Log.d("test", taskSnapshot.toString())
        }
        bitmapP?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        mountainsRef = storageRef?.child("imagenesPerro/" + edtNombreR.text.toString() + "P.jpeg")
        Log.d("test", "filepath upload ${filePathP}")
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
                    Log.d("test", "filepath: ${filePathP}")
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