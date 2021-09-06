package com.sonicmaster.herokuapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.sonicmaster.herokuapp.data.UserPreferences
import com.sonicmaster.herokuapp.data.network.RemoteDataSource
import com.sonicmaster.herokuapp.data.repository.BaseRepository
import com.sonicmaster.herokuapp.ui.auth.AuthActivity
import com.sonicmaster.herokuapp.ui.startNewActivity
import kotlinx.coroutines.launch

abstract class BaseFragment<VM : BaseViewModel,
        B : ViewBinding,
        R : BaseRepository> : Fragment() {

    protected lateinit var binding: B
    protected val remoteDataSource = RemoteDataSource()
    protected lateinit var viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepo())

        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        return binding.root
    }

    fun logout() = lifecycleScope.launch {
        UserPreferences.clear(requireContext())
        requireActivity().startNewActivity(AuthActivity::class.java)

    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getFragmentRepo(): R
}