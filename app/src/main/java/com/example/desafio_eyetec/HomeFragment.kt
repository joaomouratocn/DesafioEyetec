package com.example.desafio_eyetec

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.desafio_eyetec.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private val navController by lazy { findNavController() }

    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewUser.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToInsertUpdateUserFragment()
            navController.navigate(directions)
        }

        binding.btnAllUsers.setOnClickListener {
            // Navegar para todos usuários fragment
            // val navController = Navigation.findNavController(view)
            // navController.navigate(R.id.action_home_to_todos_usuarios)
        }
    }
}