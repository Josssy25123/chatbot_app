// File: app/src/main/java/com/example/chatbotapp/auth/AuthService.kt
package com.example.chatbotapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Get current user
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Check if user is signed in
    val isUserSignedIn: Boolean
        get() = currentUser != null

    // Sign up with email and password
    suspend fun signUp(email: String, password: String, fullName: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Create user profile in Firestore
                val userProfile = hashMapOf(
                    "fullName" to fullName,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis(),
                    "profileImageUrl" to ""
                )

                firestore.collection("users")
                    .document(user.uid)
                    .set(userProfile)
                    .await()

                AuthResult.Success(user)
            } else {
                AuthResult.Error("Failed to create user")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign up failed")
        }
    }

    // Sign in with email and password
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Failed to sign in")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }

    // Sign out
    fun signOut() {
        auth.signOut()
    }

    // Get user profile
    suspend fun getUserProfile(): UserProfile? {
        return try {
            val user = currentUser ?: return null
            val document = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()

            if (document.exists()) {
                UserProfile(
                    uid = user.uid,
                    fullName = document.getString("fullName") ?: "",
                    email = document.getString("email") ?: "",
                    profileImageUrl = document.getString("profileImageUrl") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Update user profile
    suspend fun updateUserProfile(fullName: String, profileImageUrl: String = ""): Boolean {
        return try {
            val user = currentUser ?: return false
            val updates = hashMapOf<String, Any>(
                "fullName" to fullName,
                "profileImageUrl" to profileImageUrl,
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(user.uid)
                .update(updates)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}

// Auth result sealed class
sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

// User profile data class
data class UserProfile(
    val uid: String,
    val fullName: String,
    val email: String,
    val profileImageUrl: String
)