package com.example.chatbotapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserCommand(
    val id: String = "",
    val trigger: String = "",
    val response: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

class CommandRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun userCommandsRef() = auth.currentUser?.uid?.let {
        db.collection("users").document(it).collection("commands")
    }

    suspend fun getCommands(): List<UserCommand> {
        val ref = userCommandsRef() ?: return emptyList()
        val snap = ref.get().await()
        return snap.documents.mapNotNull { it.toObject(UserCommand::class.java) }
    }

    suspend fun saveCommand(trigger: String, response: String) {
        val ref = userCommandsRef() ?: return
        ref.document(trigger).set(UserCommand(trigger, response)).await()
    }

    suspend fun deleteCommand(trigger: String) {
        val ref = userCommandsRef() ?: return
        ref.document(trigger).delete().await()
    }
}

class KnowledgeBaseRepository(private val db: FirebaseFirestore) {
    fun getUserKnowledgeBase(uid: String) =
        db.collection("users").document(uid).collection("knowledgeBase")

    suspend fun addKnowledge(uid: String, entry: KnowledgeEntry) {
        getUserKnowledgeBase(uid).add(entry).await()
    }

    suspend fun getAllKnowledge(uid: String): List<KnowledgeEntry> {
        val snapshot = getUserKnowledgeBase(uid).get().await()
        return snapshot.toObjects(KnowledgeEntry::class.java)
    }
}

data class KnowledgeEntry(
    val topic: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
