package com.example.project

import java.io.Serializable

data class Exercise(
    val name: String = "",
    val time: String = "",
    val imageUrl: String = "",
    val order: Int = 0
) : Serializable
