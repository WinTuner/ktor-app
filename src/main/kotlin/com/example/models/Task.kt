package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val content: String,
    val isDone: Boolean
)

@Serializable
data class TaskRequest(
    val content: String,
    val isDone: Boolean
)
