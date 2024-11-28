package com.dicoding.picodiploma.loginwithanimation.data.pref

data class UserModel(
    val email: String,
    val password: String,
    val token: String,
    val isLogin: Boolean = false
)