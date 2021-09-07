package com.sonicmaster.herokuapp.data.responses

import com.sonicmaster.herokuapp.data.model.Post

data class PostsResponse(
    val message: String,
    val posts: MutableList<Post>,
    val totalItems: Int,
    val totalPages: Int
)