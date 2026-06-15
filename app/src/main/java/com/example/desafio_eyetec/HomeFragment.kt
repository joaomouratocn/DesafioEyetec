package com.example.desafio_eyetec

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.desafio_eyetec.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewUser.setOnClickListener {
            // Navegar para novo usuário fragment
            // val navController = Navigation.findNavController(view)
            // navController.navigate(R.id.action_home_to_novo_usuario)
        }

        binding.btnAllUsers.setOnClickListener {
            // Navegar para todos usuários fragment
            // val navController = Navigation.findNavController(view)
            // navController.navigate(R.id.action_home_to_todos_usuarios)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}