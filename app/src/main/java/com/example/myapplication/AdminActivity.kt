package com.example.myapplication

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.OutputStream
import java.util.*


//TODO: Refactorizar. Desacoplar métodos relativos a permisos y utilización de recursos.
class AdminActivity : BaseOnBackPressedActivity() {

    private var fromPaused = false

    val CAMERA_REQUEST_CODE = 0
    val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1
    val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2

    lateinit var img : ImageView
    lateinit var uri : Uri
    lateinit var imgName : String
    lateinit var downloadUri : Uri

    //Launcher para acceder a la galería
    private val openGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK){

                //Asigna los datos devueltos un ImageView
                img.setImageURI(it.data?.data)
                //Guarda la URI de la imagen
                uri = it.data?.data!!

            }
        }

    //Launcher para acceder a la cámara
    private val openCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {

                val data = it.data!!
                val bitmap = data.extras!!.get("data") as Bitmap
                //Muestra el thumbnail de la imagen
                img.setImageBitmap(bitmap)
                //Guarda la imagen tomada a la galería
                uri = savePhotoToGallery(data)

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        img = findViewById(R.id.iv_imagen)

        //Botón de tomar foto con cámara
        val btnCamara = findViewById<ImageButton>(R.id.ib_seleccionar_foto)
        btnCamara.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            else {
                checkPermission(Manifest.permission.CAMERA)
            }
        }

        //Botón de seleccionar imagen de la galería
        val btnImagen = findViewById<ImageButton>(R.id.ib_seleccionar_imag)
        btnImagen.setOnClickListener {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onPause() {
        super.onPause()
        fromPaused = true
    }

    override fun onResume() {
        super.onResume()

        if (fromPaused && ::uri.isInitialized){

            //Recupera el nombre del archivo
            var uploadFilename = getFilenameFromUri(uri)!!

            //Escribe el nombre del archivo en el campo de texto
            val tilNombreImagen = findViewById<TextInputLayout>(R.id.outlinedTF_Nombre_Imagen)
            tilNombreImagen.editText?.setText(uploadFilename)

            //Cambia el nombre de la subida si el usuario lo modifica
            tilNombreImagen.editText?.doOnTextChanged() { text, start, before, count ->
                uploadFilename = text.toString()
            }

            //Botón de subir imagen al bucket
            val btnSubirImagen = findViewById<Button>(R.id.btn_subir)
            btnSubirImagen.setOnClickListener {
                uploadImageToBucket(uri, uploadFilename)
            }

            val tilNombreActividad = findViewById<TextInputLayout>(R.id.outlinedTF_Nombre_Actividad)

            //Botón de subir actividad
            val btnSubirActividad = findViewById<Button>(R.id.btn_subir_actividad)
            btnSubirActividad.setOnClickListener {
                val db = Firebase.firestore
                val activities = db.collection("actividades")
                val activity = hashMapOf(
                    "id" to UUID.randomUUID().toString(),
                    "titulo" to tilNombreActividad.editText?.text.toString(),
                    "desc" to "descripción",
                    "src" to downloadUri,
                    //Posicionamiento por defecto puesto que no pueden ser nulo
                    "latitude" to 37.1566977085186,
                    "longitude" to -1.8267970903259059
                )

                activities.add(activity)
                    .addOnSuccessListener { Toast.makeText(this, "Actividad subida", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { Toast.makeText(this, "Algo ha ido mal", Toast.LENGTH_SHORT).show() }
            }
        }
        fromPaused = false

    }

    //Comprueba si se tiene concedido un permiso
    private fun checkPermission(permiso : String) {
        if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED){
            //El permiso no está concedido, lo pide
            requestPermission(permiso)
        }
        else {
            //El permiso se ha concedido, realiza una acción en función del mismo
            if (permiso.equals(Manifest.permission.READ_EXTERNAL_STORAGE))
                dispatchPickImageIntent()
            else if (permiso.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //Pasa directamente a comprobar el permiso de acceso a la cámara
                checkPermission(Manifest.permission.CAMERA)
            }
            else if (permiso.equals(Manifest.permission.CAMERA))
                dispatchTakePhotoIntent()
        }
    }

    //Pide el permiso si no se ha denegado previamente
    private fun requestPermission(permiso: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)){
            //El usuario ha denegado el permiso
            Toast.makeText(this, "Necesario permiso de acceso a cámara en ajustes", Toast.LENGTH_LONG).show()
        }
        else {
            //No se ha aceptado ni denegado permiso
            when (permiso) {
                Manifest.permission.CAMERA -> {
                    ActivityCompat.requestPermissions(this, arrayOf(permiso) , CAMERA_REQUEST_CODE)
                }
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    ActivityCompat.requestPermissions(this, arrayOf(permiso), READ_EXTERNAL_STORAGE_REQUEST_CODE)
                }
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                    ActivityCompat.requestPermissions(this, arrayOf(permiso), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Comprueba la respuesta en función de la petición correspondiente
        when (requestCode) {

            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permiso aceptado
                    dispatchTakePhotoIntent()
                }
                else {
                    AlertDialog.Builder(this).apply {
                        setTitle("Permiso denegado")
                        setMessage("Se requiere acceso a la cámara. Puedes conceder el permiso desde los ajustes de la aplicación")
                        setNeutralButton(android.R.string.ok, null)
                    }.show()
                }
            }

            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permiso aceptado
                    dispatchPickImageIntent()
                }
                else {
                    AlertDialog.Builder(this).apply {
                        setTitle("Permiso denegado")
                        setMessage("Se requiere acceso al almacenamento externo. Puedes conceder el permiso desde los ajustes de la aplicación")
                        setNeutralButton(android.R.string.ok, null)
                    }.show()
                }
            }

            WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permiso aceptado
                    checkPermission(Manifest.permission.CAMERA)
                }
                else {
                    AlertDialog.Builder(this).apply {
                        setTitle("Permiso denegado")
                        setMessage("Se requiere acceso al almacenamento externo. Puedes conceder el permiso desde los ajustes de la aplicación")
                        setNeutralButton(android.R.string.ok, null)
                    }.show()
                }
            }

            else -> {
                Toast.makeText(this, "Algo ha salido mal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchPickImageIntent() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        openGallery.launch(intent)
    }
    //TODO: Meter este método en una clase de utilidades de fotos

    private fun dispatchTakePhotoIntent() {

        //Crea el intent
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Lo lanza
        openCamera.launch(takePhotoIntent)
    }
    //TODO: Meter este método en una clase de utilidades de fotos

    private fun savePhotoToGallery(data: Intent) : Uri {

        //Crea un archivo en el directorio indicado
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("IMG" + System.currentTimeMillis(), ".jpg", dir)

        //Almacena los valores de la imagen
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        //Toma la URI del archivo
        val photoUri = FileProvider.getUriForFile(
            this,
            "com.example.myapplication.fileprovider",
            file)
        //Asigna la URI del archivo creado a la imagen tomada
        data.setData(photoUri)

        //Inserta los valores de la imagen en MediaStore y crea un OutputStream
        var outputStream : OutputStream
        application.contentResolver.also {
            uri = it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
            outputStream = it.openOutputStream(uri)!!
        }
        //Comprime y escribe el bitmap de la imagen
        outputStream.use { output ->
            val bitmap = data.extras!!.get("data") as Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        }

        //Marca la imagen como no pendiente para su utilización
        values.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.IS_PENDING,0)
        }
        contentResolver.update(uri, values,null,null)

        return photoUri
    }
    //TODO: Meter este método en una clase de utilidades de fotos

    //Devuelve el nombre del archivo correspondiente a un URI
    private fun getFilenameFromUri (uri: Uri) : String? {
        var filename : String? = null

        val cursor = application.contentResolver.query(uri, null, null, null, null)
        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1){
                    filename = cursor.getString(displayNameIndex)
                }
            }
        }
        return filename
    }
    //TODO: Meter este método en una clase de utilidades de fotos

    /*  private fun setUploadFilename (uri : Uri): String {

          //Recupera el nombre del archivo
          var uploadFilename = getFilenameFromUri(uri)!!

          //Escribe el nombre del archivo en el campo de texto
          val tilNombreImagen = findViewById<TextInputLayout>(R.id.outlinedTF_Nombre_Imagen)
          tilNombreImagen.editText?.setText(uploadFilename)

          //Cambia el nombre de la subida si el usuario lo modifica
          tilNombreImagen.editText?.doOnTextChanged() { text, start, before, count ->
              uploadFilename = text.toString()
          }


          return uploadFilename
      } */

    private fun uploadImageToBucket (uri : Uri, uploadFilename: String) {

        //Crea una instancia de FirebaseStorage
        val storage = Firebase.storage
        //Crea una referencia a Firebase Storage
        val storageRef = storage.reference
        //Crea una referencia a un archivo en Firebase Storage con el nombre especificado
        val imageRef = storageRef.child(uploadFilename)
        //Sube el archivo al bucket
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Se ha subido la imagen al bucket", Toast.LENGTH_SHORT).show()
                imageRef.downloadUrl.addOnCompleteListener { download ->
                    if (download.isSuccessful){
                        downloadUri = download.result
                        Toast.makeText(this, "Todo bien", Toast.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(this, "Todo mal", Toast.LENGTH_SHORT).show()
                }
            }
            else
                Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_SHORT).show()
        }

    }

    //Modifica la funcionalidad del botón Atrás
    override fun onBackPressed() {
        onTwiceBackPressed(this)
    }

}

