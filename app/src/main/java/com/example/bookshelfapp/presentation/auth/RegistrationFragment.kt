package com.example.bookshelfapp.presentation.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookshelfapp.R
import com.example.bookshelfapp.data.models.AuthState
import com.example.bookshelfapp.databinding.FragmentRegistrationBinding
import com.example.bookshelfapp.utils.checkIfPasswordIsValid
import com.example.bookshelfapp.utils.checkIfEmailIsValid
import com.example.bookshelfapp.utils.setTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null

    private val viewModel by viewModels<AuthViewModel>()
    private val binding get() = _binding!!
    var countrySelected: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.countryList
        )
        binding.countrySpinner.adapter = adapter
        binding.etPassword.setTextWatcher(binding.etPassword)
        binding.etEmail.setTextWatcher(binding.etEmail)

        setOnClickListeners()
        setObservers()


    }

    fun setOnClickListeners() {
        binding.countrySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                countrySelected = viewModel.countryList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        binding.signupButton.setOnClickListener {

            val userName = binding.etUserName.editText?.text.toString()
            val email = binding.etEmail.editText?.text.toString()
            val password = binding.etPassword.editText?.text.toString()

            val passwordErrorMessage = password.checkIfPasswordIsValid()
            val emailErrorMessage = email.checkIfEmailIsValid()

            if (passwordErrorMessage.isNotBlank() || emailErrorMessage.isNotBlank()) {
                if (passwordErrorMessage.isNotBlank()) {
                    binding.etPassword.apply {
                        isErrorEnabled = true
                        error = passwordErrorMessage
                    }
                }

                if (emailErrorMessage.isNotBlank()) {
                    binding.etEmail.apply {
                        isErrorEnabled = true
                        error = emailErrorMessage
                    }
                }
            } else {
                viewModel.register(
                    email = email,
                    userName = userName,
                    password = password,
                    country = countrySelected
                )
            }

        }

        binding.tvLoginButton.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else {
                findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.registrationFlow.collectLatest {
                when (it) {
                    is AuthState.Failure -> {
                        hideProgressBar()
                        Toast.makeText(
                            requireContext(),
                            it.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    AuthState.Loading -> {
                        showProgressBar()
                    }
                    is AuthState.Success -> {
                        hideProgressBar()
                    }
                    null -> {
                        hideProgressBar()
                        Log.d("Registration", "Failure")
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.clProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.clProgressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}