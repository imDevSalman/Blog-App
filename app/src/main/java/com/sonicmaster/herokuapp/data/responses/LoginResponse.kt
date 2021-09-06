package com.sonicmaster.herokuapp.data.responses

import com.sonicmaster.herokuapp.data.model.User

class LoginResponse(val token: String, val user: User)