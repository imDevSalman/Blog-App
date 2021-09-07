package com.sonicmaster.herokuapp.data.repository

import com.sonicmaster.herokuapp.data.network.UserApi
import com.sonicmaster.herokuapp.data.responses.PostsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class PostRepository(private val api: UserApi) : BaseRepository() {
    suspend fun getPosts(page: Int) = safeApiCall { api.getPosts(page) }


    suspend fun createPost(title: RequestBody, content: RequestBody, image: MultipartBody.Part) =
        safeApiCall { api.createPost(title, content, image) }

    suspend fun getPost(postId: String) = safeApiCall { api.getPost(postId) }

    suspend fun deletePost(postId: String) = safeApiCall { api.deletePost(postId) }
}