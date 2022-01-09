package com.example.nearbyworkers.model

data class Worker (

    var account: String = "",
    var category:String="",
    var description:String="",
    var profileImageUrl: String = "",
    var username:String="",
    var lat:Double=0.0,
    var lon:Double=0.0,
    var access:Boolean=false

)