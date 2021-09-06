package com.sonicmaster.herokuapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.sonicmaster.herokuapp.data.network.AuthApi
import com.sonicmaster.herokuapp.data.network.Resource
import com.sonicmaster.herokuapp.data.repository.AuthRepository
import com.sonicmaster.herokuapp.databinding.FragmentRegisterBinding
import com.sonicmaster.herokuapp.ui.base.BaseFragment
import com.sonicmaster.herokuapp.ui.enable
import com.sonicmaster.herokuapp.ui.handleApiError
import com.sonicmaster.herokuapp.ui.home.HomeActivity
import com.sonicmaster.herokuapp.ui.startNewActivity
import com.sonicmaster.herokuapp.ui.visible
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<AuthViewModel, FragmentRegisterBinding, AuthRepository>() {

    private lateinit var email: String
    private lateinit var password: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            registerProgress.visible(false)
            registerButton.enable(false)

            passwordEditText.addTextChangedListener {
                val name = nameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()

                registerButton.enable(
                    name.isNotEmpty() && email.isNotEmpty() && it.toString().trim().isNotEmpty()
                )
            }
        }

        binding.registerButton.setOnClickListener {
            register()
        }

        viewModel.registerResponse.observe(viewLifecycleOwner, {
            binding.registerProgress.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "message: ${it.value.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    viewModel.login(email, password)
                    viewModel.loginResponse.observe(viewLifecycleOwner, {
                        when (it) {
                            is Resource.Success -> {
                                lifecycleScope.launch {
                                    viewModel.saveAuthToken(it.value.token)
                                    requireActivity().startNewActivity(HomeActivity::class.java)
                                }
                            }
                            is Resource.Failure -> handleApiError(it) { }
                        }
                    })
                }
                is Resource.Failure -> {

                }
            }
        })

    }

    private fun register() {
        val name = binding.nameEditText.text.toString().trim()
        email = binding.emailEditText.text.toString().trim()
        password = binding.emailEditText.text.toString().trim()

        viewModel.register(name, email, password)

    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

    override fun getFragmentRepo() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), requireContext())

}