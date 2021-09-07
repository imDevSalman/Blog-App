package com.sonicmaster.herokuapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sonicmaster.herokuapp.data.network.Resource
import com.sonicmaster.herokuapp.data.repository.PostRepository
import com.sonicmaster.herokuapp.data.responses.CreatePostResponse
import com.sonicmaster.herokuapp.data.responses.PostResponse
import com.sonicmaster.herokuapp.data.responses.PostsResponse
import com.sonicmaster.herokuapp.ui.base.ApiStatus
import com.sonicmaster.herokuapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class HomeViewModel(private val repository: PostRepository) : BaseViewModel(repository) {
    private val _status = MutableLiveData<ApiStatus>()
    private val _posts: MutableLiveData<Resource<PostsResponse>> = MutableLiveData()
    private val _post: MutableLiveData<Resource<CreatePostResponse>> = MutableLiveData()
    private val _singlePost: MutableLiveData<Resource<PostResponse>> = MutableLiveData()
    private val _delete: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()

    val status: LiveData<ApiStatus> = _status
    val posts: LiveData<Resource<PostsResponse>> = _posts
    val post: LiveData<Resource<CreatePostResponse>> = _post
    val singlePost: LiveData<Resource<PostResponse>> = _singlePost
    val delete: LiveData<Resource<ResponseBody>> = _delete


    fun getPosts(page: Int) = viewModelScope.launch {
        _status.value = ApiStatus.LOADING
        _posts.value = Resource.Loading
        try {
            _posts.value = repository.getPosts(page)
            _status.value = ApiStatus.DONE
        } catch (e: Exception) {
            _status.value = ApiStatus.ERROR
            _posts.value = null
        }
    }

//    private fun handlePostsResponse(response: Response<PostsResponse>): Resource<PostsResponse> {
//        if (response.isSuccessful) {
//            response.body()?.let { result ->
//                postsPage++
//                if (postsResponse == null) {
//                    postsResponse = result
//                } else {
//                    val oldPosts = postsResponse?.posts
//                    val newPosts = result.posts
//                    oldPosts?.addAll(newPosts)
//                }
//                return Resource.Success(postsResponse ?: result)
//            }
//        }
//        return Resource.Failure(false, response.code(), response.errorBody())
//    }

    fun createPost(title: RequestBody, content: RequestBody, image: MultipartBody.Part) =
        viewModelScope.launch {
            _post.value = Resource.Loading
            _post.value = repository.createPost(title, content, image)
        }

    fun getPost(postId: String) {
        viewModelScope.launch {
            _singlePost.value = Resource.Loading
            _singlePost.value = repository.getPost(postId)
        }
    }

    fun deletePost(postId: String) = viewModelScope.launch {
        _delete.value = Resource.Loading
        _delete.value = repository.deletePost(postId)
    }
}