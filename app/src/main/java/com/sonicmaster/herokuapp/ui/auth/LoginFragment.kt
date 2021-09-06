package com.sonicmaster.herokuapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sonicmaster.herokuapp.R
import com.sonicmaster.herokuapp.data.network.AuthApi
import com.sonicmaster.herokuapp.data.network.Resource
import com.sonicmaster.herokuapp.data.repository.AuthRepository
import com.sonicmaster.herokuapp.databinding.FragmentLoginBinding
import com.sonicmaster.herokuapp.ui.base.BaseFragment
import com.sonicmaster.herokuapp.ui.enable
import com.sonicmaster.herokuapp.ui.handleApiError
import com.sonicmaster.herokuapp.ui.home.HomeActivity
import com.sonicmaster.herokuapp.ui.startNewActivity
import com.sonicmaster.herokuapp.ui.visible
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            loginProgress.visible(false)
            loginButton.enable(false)

            passwordEditText.addTextChangedListener {
                val email = emailEditText.text.toString().trim()
                loginButton.enable(email.isNotEmpty() && it.toString().isNotEmpty())
            }

            loginButton.setOnClickListener {
                login()
            }

            registerText.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }


        viewModel.loginResponse.observe(viewLifecycleOwner, {
            binding.loginProgress.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        viewModel.saveAuthToken(it.value.token)
                        requireActivity().startNewActivity(HomeActivity::class.java)
                    }
                }
                is Resource.Failure -> handleApiError(it) { login() }
            }
        })
    }

    private fun login() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        println("debug: email-->$email and password-->$password")

        viewModel.login(email, password)
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepo() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), requireContext())

}