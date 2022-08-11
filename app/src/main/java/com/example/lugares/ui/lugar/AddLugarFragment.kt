package com.example.lugares.ui.lugar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lugares.R
import com.example.lugares.databinding.FragmentAddLugarBinding
import com.example.lugares.model.Lugar
import com.example.lugares.viewmodel.LugarViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lugares.utiles.AudioUtiles
import com.lugares.utiles.ImagenUtiles

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddLugarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddLugarFragment : Fragment() {
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var lugarViewModel: LugarViewModel

    //Nuevas variables//

    //creando instancias de las clases que tenemos nuevas
    private lateinit var audioUtiles: AudioUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>
    private lateinit var imagenUtiles: ImagenUtiles

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saveInstanceState: Bundle?
    ) : View {
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        binding.finalAddLugarBtn.setOnClickListener {
            if( (binding.lugarName.text.toString().isNotEmpty())) {

                binding.progressBar.visibility = ProgressBar.VISIBLE
                binding.msgMensaje.text = "Subiendo nota de audio"
                binding.msgMensaje.visibility = TextView.VISIBLE
            }
        }

        subeAudioNube()

        audioUtiles = AudioUtiles(
            requireActivity(), requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            "Grabando nota de audio",
            "Nota de audio detenida"
        )

        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                imagenUtiles.actualizaFoto()
            }
        }

        imagenUtiles = imagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotarl,
            binding.btRotarR,
            binding.imagen,
            tomarFotoActivity
        )


        return binding.root
    }


    private fun subeAudioNube(){
        val audioFile = audioUtiles.audioFile
        if (audioFile.exists() && audioFile.isFile && audioFile.canRead()) { //significa que se puede leer
            val ruta  = Uri.fromFile(audioFile)


            val referencia: StorageReference
            = Firebase.storage.reference.child(
                "lugaresApp/${Firebase.auth.currentUser?.uid}/audios/${audioFile.name}"
            )

            val uploadTask = referencia.putFile(ruta)
            uploadTask
                .addOnSuccessListener {
                    val downloadUrl = referencia.downloadUrl
                    downloadUrl.addOnSuccessListener {
                            val rutaNota = it.toString()
                            subeImagenNube(rutaNota)
                        }
                    }


            uploadTask
                .addOnFailureListener{
                    Toast.makeText(context, "Error subiendo nota", Toast.LENGTH_SHORT).show()
                    subeImagenNube("")
                    }
                }else{
                    Toast.makeText(context, "No se sube nota de audio", Toast.LENGTH_SHORT).show()
            subeImagenNube("")
        }
    }


    private fun subeImagenNube(rutaAudio: String) {
        binding.msgMensaje.text = "Subiendo imagen"

        val imageFile = imagenUtiles.imagenFile
        if (imageFile.exists() && imageFile.isFile && imageFile.canRead()) { //significa que se puede leer
            val ruta  = Uri.fromFile(imageFile)


            val referencia: StorageReference
                    = Firebase.storage.reference.child(
                "lugaresApp/${Firebase.auth.currentUser?.uid}/imagenes/${imageFile.name}"
            )

            val uploadTask = referencia.putFile(ruta)

            uploadTask
                .addOnSuccessListener {
                    val downloadUrl = referencia.downloadUrl
                    downloadUrl.addOnSuccessListener {
                            val rutaImagen = it.toString()
                            agregarLugar(rutaAudio, rutaImagen)
                        }
                }

            uploadTask
                .addOnFailureListener{
                    Toast.makeText(context, "Error subiendo imagen", Toast.LENGTH_SHORT).show()
                    agregarLugar(rutaAudio, "")
                }
        }else{
            Toast.makeText(context, "No se sube imagen", Toast.LENGTH_SHORT).show()
            agregarLugar(rutaAudio, "")
        }


    }



    private fun addLugar() {
        val nombre = binding.lugarName.text.toString()

        if(validation(nombre)) {
            val lugar = Lugar("", nombre)
            lugarViewModel.addLugar(lugar)
            Toast.makeText(requireContext(), getString(R.string.msg_lugar_added), Toast.LENGTH_LONG).show()

            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun validation (nombre: String): Boolean {
        return !(nombre.isEmpty())
    }

    private fun agregarLugar(rutaAudio: String, rutaImagen: String){


    }

}