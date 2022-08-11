package com.lugares.utiles

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.example.lugares.BuildConfig
import java.io.File

class ImagenUtiles (
    private val contexto: Context,
    btPhoto: ImageButton,
    btRotaL: ImageButton,
    btRotaR: ImageButton,
    private val imagen: ImageView,
    private var tomarFotoActivity: ActivityResultLauncher<Intent>
) {
    init {
        btPhoto.setOnClickListener { tomarFoto() } //para tomar foto
        btRotaL.setOnClickListener { imagen.rotation=imagen.rotation-90f } //rotacion de 90 grados a la izq
        btRotaR.setOnClickListener { imagen.rotation=imagen.rotation+90f } //rotacion de 90 grados a la derecha
    }

    lateinit var imagenFile: File
    private lateinit var currentPhotoPath: String

    @SuppressLint("QueryPermissionsNeeded")
    private fun tomarFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(contexto.packageManager) != null) {
            imagenFile = createImageFile() //se crea la instancia desde el image file
            val photoURI = FileProvider.getUriForFile(
                contexto,
                BuildConfig.APPLICATION_ID + ".provider",
                imagenFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI) //tire esta foto muestre la imagen
            tomarFotoActivity.launch(intent)
        }
    }

    private fun createImageFile(): File { //hace el uso del file explorer para guardar la foto
        val archivo=OtrosUtiles.getTempFile("imagen_")
        val storageDir: File? =
            contexto.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image= File.createTempFile(
            archivo,       /* prefijo */
            ".jpg",  /* extensión */
            storageDir    /* directorio */)
        currentPhotoPath = image.absolutePath
        return image
    }

    fun actualizaFoto() {
        imagen.setImageBitmap(
            BitmapFactory.decodeFile(imagenFile.absolutePath))
    }
}








