package com.src.localite

data class UserProfile(var name: String, var email: String, var photoUrl: String, var tel: String){
    constructor() : this("", "", "", "")
}