package com.nanta.post8

data class Task(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var deadline: String? = null,
    var done: Boolean = false
)