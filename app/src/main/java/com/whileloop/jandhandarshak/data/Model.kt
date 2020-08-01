package com.whileloop.jandhandarshak.data

data class Model(

    var htmlAttributions: List<Any>? = null,
    var results: List<Any>? = null,
    var status: String? = null
)

data class Result(
    var business_status: String? = null,
    var geometry: String? = null,
    var viewport: String? = null,
    var name: String? = null


)

