package com.sonicmaster.herokuapp.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
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

    private val args: PostFragmentArgs by navArgs()
    lateinit var postId: String
    private var isEditable: Boolean = true


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
                        setHasOptionsMenu(it.value.editable)
                    }
                }
                is Resource.Failure -> {
                    binding.postProgress.visible(false)
                    println(it.errorBody?.string())
                }
            }
        })


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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.post_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_editPost -> {
                isEditable = !isEditable
                if (isEditable) {
                    item.setIcon(R.drawable.ic_baseline_edit_24)
                    binding.apply {
                        titleEdt.isFocusableInTouchMode = false
                        contentEdt.isFocusableInTouchMode = false
                        titleEdt.clearFocus()
                        contentEdt.clearFocus()
                    }
                    hideKeyboard(requireContext(), requireView())
                } else {
                    item.setIcon(R.drawable.ic_baseline_done_24)
                    binding.apply {
                        titleEdt.isFocusableInTouchMode = true
                        contentEdt.isFocusableInTouchMode = true
                    }
                }

            }
            R.id.action_deletePost -> {
                showDialog()
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}