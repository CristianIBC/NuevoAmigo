package mx.tec.nuevoamigo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_registrar_perrita.*
import java.io.IOException
import java.util.*

class RegistrarPerrita : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var filePathP: Uri? = null
    private var filePathF: Uri? = null

    private var boton: Int = 0
    var bitmapP: Bitmap? = null
    var bitmap: Bitmap? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_perrita)
        val datosSpinner = arrayListOf("Pequeño", "Mediano", "Grande","Gigante")
        val adaptadorSpinner = ArrayAdapter(this, android.R.layout.simple_list_item_1, datosSpinner)
        spnrTamaño.adapter = adaptadorSpinner

        btnRegistrar.setOnClickListener {
            var sexo = when(radioGroup.checkedRadioButtonId){
                R.id.rGHembraR -> "Hembra"
                R.id.rGMachoR -> "Macho"
                else -> ""
            }
            if(edtNombreR.text.toString()!="" && edtRazaR.text.toString()!="" && edtDescripcionR.text.toString()!="" && edtEdadR.text.toString()!="" && sexo!="" && bitmapP!=null && bitmap!=null){
                filePath=filePathF
                uploadImage()
            }else{
                Toast.makeText(this,"No puedes dejar campos en blanco (incluidas imagenes)",Toast.LENGTH_LONG).show()
            }

        }

        btnFotoPR.setOnClickListener {
            boton = 1
            Log.d("test","previo: ${boton}")
            launchGallery()
            //boton = 0
        }
        btnFotoR.setOnClickListener {
            boton=2
            launchGallery()
            //boton=0
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            Log.d("test",boton.toString())
            if (boton==1){
                try {
                    bitmapP = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Log.d("test",bitmapP.toString())
                    imgPR.setImageBitmap(bitmapP)
                    filePathP=filePath
                    //uploadImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if(boton==2){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Log.d("test",bitmap.toString())
                    imgR.setImageBitmap(bitmap)
                    filePathF=filePath
                    //uploadImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            boton=0
            filePath = null
            Toast.makeText(this,"Foto agregada",Toast.LENGTH_SHORT).show()
        }
    }
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("imagenesPerro/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    //addUploadRecordToDb(downloadUri.toString())
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUploadRecordToDb(uri: String){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.collection("posts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }

}