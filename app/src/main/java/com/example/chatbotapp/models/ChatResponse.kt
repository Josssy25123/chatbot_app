package com.example.chatbotapp.models

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
