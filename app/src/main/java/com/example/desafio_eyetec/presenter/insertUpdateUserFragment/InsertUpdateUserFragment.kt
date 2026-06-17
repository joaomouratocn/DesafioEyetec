package com.example.desafio_eyetec.presenter.insertUpdateUserFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.desafio_eyetec.R
import com.example.desafio_eyetec.databinding.FragmentInsertUpdateUserBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class InsertUpdateUserFragment : Fragment() {
    private val args: InsertUpdateUserFragmentArgs by navArgs()
    private val navController by lazy { findNavController() }
    private val productViewModel: InsertUpdateUserViewModel by viewModel { parametersOf(args.userId) }
    private val binding by lazy { FragmentInsertUpdateUserBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initConfig()
        observeViewModel()
    }

    private fun observeViewModel() {
        productViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.edtName.setText(it.name)
                binding.edtAge.setText(it.age.toString())
                binding.edtEmail.setText(it.email)
                binding.ckbEnable.isChecked = it.enable

                binding.btnSave.text = getString(R.string.update)
                (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = getString(R.string.edit_user)
            }
        }

        productViewModel.photoPath.observe(viewLifecycleOwner) { path ->
            if (path != null) {
                binding.imgPhoto.setImageURI(Uri.parse(path))
                binding.btnDeletePhoto.visibility = View.VISIBLE
            } else {
                binding.imgPhoto.setImageResource(R.drawable.ic_person)
                binding.btnDeletePhoto.visibility = View.GONE
            }
        }

        // Observe result from CameraFragment
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("photoPath")
            ?.observe(viewLifecycleOwner) { path ->
                productViewModel.setPhotoPath(path)
            }
    }

    private fun initConfig(){
        binding.btnTakePhoto.setOnClickListener {
            val directions = InsertUpdateUserFragmentDirections.actionInsertUpdateUserFragmentToCameraFragment()
            navController.navigate(directions)
        }

        binding.btnDeletePhoto.setOnClickListener {
            productViewModel.deletePhoto()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtName.text.toString()
            val age = binding.edtAge.text.toString().toIntOrNull() ?: 0
            val email = binding.edtEmail.text.toString()
            val enable = binding.ckbEnable.isChecked

            if (productViewModel.isInvalidName(name)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.invalid_name),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (productViewModel.isInvalidAge(age)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.invalid_age),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (productViewModel.isInvalidEmail(email)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.invalid_email),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                productViewModel.saveUser(name, age, email, enable)
                val message = if (args.userId == -1L) {
                    R.string.user_saved_success
                } else {
                    R.string.user_updated_success
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }

        binding.btnCancel.setOnClickListener {
            navController.popBackStack()
        }
    }
}