package com.technopolis_education.globusapp.model

data class PostRequest(
    val userEmail: String,
    val userName: String,
    val userSurname: String,
    val text: String,
    val time: String,
    val point: PointRequest?,
    val title: String
)
