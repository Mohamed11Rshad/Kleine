package com.example.kleineyt.util

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {

    return if(email.isEmpty())
        RegisterValidation.Failed("Email cannot be empty")

    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        RegisterValidation.Failed("Email is not valid")

    else
        RegisterValidation.Success

}

fun validatePassword(password : String): RegisterValidation {

    return if(password.isEmpty())
        RegisterValidation.Failed("Password cannot be empty")

    else if(password.length < 6)
        RegisterValidation.Failed("Password must be at least 6 characters")

    else
        RegisterValidation.Success

}