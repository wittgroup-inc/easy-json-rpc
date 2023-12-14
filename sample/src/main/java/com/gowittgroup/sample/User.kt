package com.gowittgroup.sample

data class User(private val userId: Int, private val userName: String){
    override fun toString(): String {
        return "User(userId=$userId, userName='$userName')"
    }
}
