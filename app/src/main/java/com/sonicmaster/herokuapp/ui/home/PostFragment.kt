package com.sonicmaster.herokuapp.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.sonicmaster.herokuapp.R
import com.sonicmaster.herokuapp.data.UserPreferences
import com.sonicmaster.herokuapp.data.network.RemoteDataSource.Companion.BASE_URL
import com.sonicmaster.herokuapp.data.network.Resource
import com.sonicmaster.herokuapp.data.network.UserApi
import com.sonicmaster.herokuapp.data.repository.PostRepository
import com.sonicmaster.herokuapp.databinding.FragmentPostBinding
import com.sonicmaster.herokuapp.ui.base.BaseFragment
import com.sonicmaster.herokuapp.ui.snackbar
import com.sonicmaster.herokuapp.ui.visible
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class PostFragment : BaseFragment<HomeViewModel, FragmentPostBinding, PostRepository>() {

    val args: PostFragmentArgs by navArgs()
    lateinit var postId: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = args.postId

        viewModel.getPost(postId)

        viewModel.singlePost.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.postProgress.visible(true)
                }
                is Resource.Success -> {
                    binding.apply {
                        postProgress.visible(false)
                        titleEdt.setText(it.value.post.title)
                        Glide.with(requireView())
                            .load(BASE_URL + it.value.post.imageUrl)
                            .into(image)
                        contentEdt.setText(it.value.post.content)
                        deletePostButton.visible(it.value.editable)
                    }
                }
                is Resource.Failure -> {
                    binding.postProgress.visible(false)
                    println(it.errorBody?.string())
                }
            }
        })

        binding.deletePostButton.setOnClickListener {
            println("debug: delete button")
            showDialog()
        }

        viewModel.delete.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.postProgress.visible(true)
                }
                is Resource.Success -> {
                    binding.postProgress.visible(false)
                    val message = JSONObject(it.value.string()).get("message")
                    requireView().snackbar(message.toString())
                    findNavController().navigate(R.id.action_postFragment_to_homeFragment)
                }
                is Resource.Failure -> {
                    binding.postProgress.visible(false)
                    println("debug : ${it.errorBody?.string()}")
                }
            }
        })

    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(context).setTitle("Delete")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton(
                "YES"
            ) { p0, p1 -> viewModel.deletePost(postId) }
            .setNegativeButton("No") { _, _ -> }
            .create()
        dialog.show()
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPostBinding.inflate(inflater, container, false)

    override fun getFragmentRepo(): PostRepository {
        val token = runBlocking {
            UserPreferences.getToken(requireContext()).first()
        }

        val api = remoteDataSource.buildApi(UserApi::class.java, token)

        return PostRepository(api)
    }

}