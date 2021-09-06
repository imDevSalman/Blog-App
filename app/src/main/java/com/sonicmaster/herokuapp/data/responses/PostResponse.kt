package com.sonicmaster.herokuapp.data.responses

import com.sonicmaster.herokuapp.data.model.Post

class PostResponse(val message: String, val post: Post, val editable: Boolean)