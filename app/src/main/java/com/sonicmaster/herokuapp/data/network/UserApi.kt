package com.sonicmaster.herokuapp.data.network

import com.sonicmaster.herokuapp.data.responses.CreatePostResponse
import com.sonicmaster.herokuapp.data.responses.PostResponse
import com.sonicmaster.herokuapp.data.responses.PostsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("feed/posts")
    suspend fun getPosts(@Query("page") page: Int): PostsResponse

//    @GET("user")
//    suspend fun getUser(): User

    @Multipart
    @POST("feed/post")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part image: MultipartBody.Part
    ): CreatePostResponse

    @GET("feed/post/{postId}")
    suspend fun getPost(@Path("postId") postId: String): PostResponse

    @DELETE("feed/post/{postId}")
    suspend fun deletePost(@Path("postId") postId: String): ResponseBody

    @POST("user/logout")
    suspend fun logout(): ResponseBody
}