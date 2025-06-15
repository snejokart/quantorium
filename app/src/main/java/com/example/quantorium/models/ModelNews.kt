package com.example.quantorium.models

import kotlinx.serialization.Serializable

@Serializable
data class ModelNews(
    val id: String,
    val title: String,
    val description: String, // Changed from "text" to "description"
    val link: String,
    val date: String // Added date field
)