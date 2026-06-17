package com.example.desafio_eyetec.presenter.allUsers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.desafio_eyetec.R
import com.example.desafio_eyetec.databinding.FragmentAllUsersBinding
import com.example.desafio_eyetec.domain.models.User
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllUsersFragment : Fragment() {
    private val navController by lazy { findNavController() }
    private val binding by lazy { FragmentAllUsersBinding.inflate(layoutInflater) }
    private val viewModel: AllUsersViewModel by viewModel()
    private val userAdapter by lazy {
        UserAdapter(
            onEditClick = { user ->
                val directions = AllUsersFragmentDirections.actionAllUsersFragmentToInsertUpdateUserFragment(user.id ?: -1L)
                navController.navigate(directions)
            },
            onDeleteClick = { user ->
                showDeleteConfirmation(user)
            }
        )
    }

    private fun showDeleteConfirmation(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.confirmar_delecao)
            .setMessage(R.string.tem_certeza_que_deseja_deletar_este_usuario)
            .setPositiveButton(R.string.sim) { _, _ ->
                viewModel.deleteUser(user)
            }
            .setNegativeButton(R.string.nao, null)
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rcvUsers.apply { adapter = userAdapter }
    }

    private fun setupSearch() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchUsers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            userAdapter.submitList(users)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAllUsers()
    }
}