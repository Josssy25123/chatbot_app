package com.example.chatbotapp.models

data class UniversityData(
    val title: String = "",
    val description: String = "",
    val department: String = "",
    val extra: Map<String, String>? = null // Optional for fields like 'electives_requirement'
)
