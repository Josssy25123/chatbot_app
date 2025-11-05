package com.example.chatbotapp.data

import com.example.chatbotapp.models.Message

object MessageMapper {
    fun toOpenAIMessage(chatMessage: ChatMessage): Message {
        return Message(
            role = if (chatMessage.isUser) "user" else "assistant",
            content = chatMessage.text
        )
    }

    fun toChatMessage(message: Message): ChatMessage {
        return ChatMessage(
            text = message.content,
            isUser = message.role == "user"
        )
    }
}