package com.example.lugares.ui.lugar

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lugares.R
import com.example.lugares.databinding.FragmentAddLugarBinding
import com.example.lugares.databinding.FragmentUpdateLugarBinding
import com.example.lugares.model.Lugar
import com.example.lugares.viewmodel.LugarViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddLugarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateLugarFragment : Fragment() {
    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var lugarViewModel: LugarViewModel

    private val args by navArgs<UpdateLugarFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saveInstanceState: Bundle?
    ) : View {
        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        binding.lugarName.setText(args.lugar.nombre)

        binding.finalAddLugarBtn.setOnClickListener { updateLugar() }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // se es el borrado
        if (item.itemId == R.id.menu_delete) {
            deleteLugar()
        }
        return  super.onOptionsItemSelected(item)
    }

    private fun updateLugar() {
        val nombre = binding.lugarName.text.toString()

        if(validation(nombre)) {
            val lugar = Lugar(args.lugar.id, nombre)
            lugarViewModel.updateLugar(lugar)
            Toast.makeText(requireContext(), getString(R.string.msg_lugar_updated), Toast.LENGTH_LONG).show()

            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteLugar(){
        val builder = AlertDialog.Builder(requireContext())

        builder.setPositiveButton(getString(R.string.si)) { _,_ ->
            lugarViewModel.deleteLugar(args.lugar)
            Toast.makeText(requireContext(), getString(R.string.deleted) + " ${args.lugar.nombre}", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }

        builder.setNegativeButton(getString(R.string.no)) { _,_ -> }
        builder.setTitle(R.string.deleted)
        builder.setMessage(getString(R.string.seguroBorrar) + " ${args.lugar.nombre}?")
        builder.create().show()
    }

    private fun validation (nombre: String): Boolean {
        return !(nombre.isEmpty())
    }

}