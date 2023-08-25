package com.example.bookshelfapp.presentation.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookshelfapp.R
import com.example.bookshelfapp.data.models.AuthState
import com.example.bookshelfapp.databinding.FragmentLoginBinding
import com.example.bookshelfapp.databinding.FragmentRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AuthViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setObservers()
    }

    private fun setOnClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.etEmail.editText?.text.toString()
            val password = binding.etPassword.editText?.text.toString()
            viewModel.login(email, password)
        }
        binding.tvSignUpButton.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else {
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.loginFlow.collectLatest {
                when (it) {
                    is AuthState.Failure -> Log.d("Login", "Failure, ${it.errorMessage}")
                    AuthState.Loading -> Log.d("Login", "Loading")
                    is AuthState.Success -> Log.d("Login", "Success")
                    null -> Log.d("Login", "null")
                }
            }
        }
    }

}