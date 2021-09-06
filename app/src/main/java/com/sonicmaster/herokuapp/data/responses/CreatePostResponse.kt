package com.sonicmaster.herokuapp.data.responses

import com.sonicmaster.herokuapp.data.model.Post

data class CreatePostResponse(
    val message: String,
    val post: Post
)
