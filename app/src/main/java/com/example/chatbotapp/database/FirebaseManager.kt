
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatRepository(private val db: FirebaseFirestore) {
    private val chatSessionsCollection = db.collection("chatSessions")

    // Function to delete a chat session by ID
    suspend fun deleteChatSession(sessionId: String) {
        try {
            // Delete the document with the given sessionId
            chatSessionsCollection.document(sessionId).delete().await()
            println("Chat session $sessionId successfully deleted from Firestore.")
        } catch (e: Exception) {
            // Handle any potential errors during deletion
            println("Error deleting chat session $sessionId: ${e.message}")
            throw e // Re-throw the exception to be handled by the caller (ViewModel)
        }
    }
}