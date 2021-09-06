package com.sonicmaster.herokuapp.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.sonicmaster.herokuapp.R
import com.sonicmaster.herokuapp.data.UserPreferences
import com.sonicmaster.herokuapp.data.adapters.PostsAdapter
import com.sonicmaster.herokuapp.data.network.Resource
import com.sonicmaster.herokuapp.data.network.UserApi
import com.sonicmaster.herokuapp.data.repository.PostRepository
import com.sonicmaster.herokuapp.databinding.FragmentHomeBinding
import com.sonicmaster.herokuapp.ui.base.BaseFragment
import com.sonicmaster.herokuapp.ui.getFileName
import com.sonicmaster.herokuapp.ui.handleApiError
import com.sonicmaster.herokuapp.ui.snackbar
import com.sonicmaster.herokuapp.ui.visible
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding, PostRepository>() {

    lateinit var postsAdapter: PostsAdapter
    lateinit var imageView: ImageView
    lateinit var imageUri: Uri


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.postsProgress.visible(false)
        initRecyclerView()

        viewModel.getPosts()

        postsAdapter.setOnItemClickListener {
            println("debug: $it")
            val bundle = Bundle().apply {
                putSerializable("postId", it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_postFragment, bundle)
        }

        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.postsProgress.visible(true)
                }
                is Resource.Success -> {
                    binding.postsProgress.visible(false)
                    Toast.makeText(requireContext(), it.value.message, Toast.LENGTH_SHORT).show()
                    postsAdapter.differ.submitList(it.value.posts)

                }
                is Resource.Failure -> {
                    handleApiError(it)
                    binding.postsProgress.visible(false)
                    val message = JSONObject(it.errorBody?.string()!!).get("message")
                    Toast.makeText(requireContext(), message.toString(), Toast.LENGTH_SHORT)
                        .show()

                    logout()
                }
            }
        })

    }

    private fun initRecyclerView() {
        postsAdapter = PostsAdapter()
        binding.postRecyclerView.apply {
            adapter = postsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_createPost -> {
                if (checkPermissions()) {
                    showDialog()
                } else {
                    requireView().snackbar("Permission required")
                }
            }
            R.id.action_logout -> {
                logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepo(): PostRepository {
        val token = runBlocking {
            UserPreferences.getToken(requireContext()).first()
        }
        val api = remoteDataSource.buildApi(
            UserApi::class.java,
            token
        )

        println("debug: token-->$token")

        return PostRepository(api)
    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
            false
        } else {
            true
        }
    }


    private fun showDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.create_post, null)
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Create Post").setPositiveButton(
                "Ok"
            ) { p0, p1 ->
                createPost(dialogView)
            }

        builder.show()


        imageView = dialogView.findViewById(R.id.post_image)
        imageView.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.blog,
                resources.newTheme()
            )
        )

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 101)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            try {
                imageUri = data?.data as Uri
                imageView.setImageURI(imageUri)
            } catch (e: Exception) {
                requireView().snackbar(e.message.toString())
            }
        }
    }


    private fun createPost(dialogView: View) {
        requireView().snackbar("createPost() called")
        val title = dialogView.findViewById<TextInputEditText>(R.id.title_edittext)
        val content = dialogView.findViewById<TextInputEditText>(R.id.content_edittext)

        val parcelFileDescriptor =
            requireActivity().contentResolver.openFileDescriptor(imageUri, "r", null) ?: return
        val file =
            File(context?.codeCacheDir, requireActivity().contentResolver.getFileName(imageUri))

        val inputString = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputString.copyTo(outputStream)

        val fileReqBody = file.asRequestBody("image/png".toMediaTypeOrNull())

        println("debug: ${file.name} ${fileReqBody.contentType()} ${fileReqBody.contentLength()}")


        viewModel.createPost(
            title.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            content.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            MultipartBody.Part.createFormData("image", file.name, fileReqBody)
        )

        viewModel.post.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.postsProgress.visible(true)
                }
                is Resource.Success -> {
                    binding.postsProgress.visible(false)
                    viewModel.getPosts()
                    requireView().snackbar(it.value.message)
                }
                is Resource.Failure -> {
                    handleApiError(it)
                    println("debug: ${it.errorCode} ${it.errorBody?.string()}")
                    binding.postsProgress.visible(false)
                }
            }
        })
    }

}