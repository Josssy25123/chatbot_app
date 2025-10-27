package com.example.chatbotapp.repository

import android.content.Context
import com.example.chatbotapp.R
import org.json.JSONObject
import org.json.JSONArray

class KnowledgeBaseRepository(private val context: Context) {

    // Load and search across all knowledge base files
    fun search(query: String): String {
        val normalizedQuery = query.lowercase()

        // Department info
        if (normalizedQuery.contains("chair")) {
            val dept = loadJson(R.raw.department)
            return dept.optString("chair", "Chair information not available.")
        }

        if (normalizedQuery.contains("dean")) {
            val dept = loadJson(R.raw.department)
            return dept.optString("dean", "Dean information not available.")
        }

        // Library info
        if (normalizedQuery.contains("library")) {
            val lib = loadJson(R.raw.library)
            return lib.optString("info", "Library details not available.")
        }

        // Registration info
        if (normalizedQuery.contains("register") || normalizedQuery.contains("enroll")) {
            val reg = loadJson(R.raw.registration)
            return reg.optString("process", "Registration process not found.")
        }

        // Calendar info
        if (normalizedQuery.contains("calendar") || normalizedQuery.contains("semester")) {
            val cal = loadJson(R.raw.morgan_state_calendar)
            return cal.optString("dates", "Calendar dates not available.")
        }

        // Classes info
        if (normalizedQuery.contains("class") || normalizedQuery.contains("course")) {
            val classes = loadArray(R.raw.classes)
            val names = (0 until classes.length()).map { i ->
                classes.getJSONObject(i).optString("name")
            }
            return "Available classes: ${names.joinToString(", ")}"
        }

        // Advising info
        if (normalizedQuery.contains("advising")) {
            val adv = loadJson(R.raw.advising)
            return adv.optString("info", "Advising details not available.")
        }

        // Degree info
        if (normalizedQuery.contains("degree")) {
            val deg = loadJson(R.raw.degree)
            return deg.optString("requirements", "Degree requirements not available.")
        }

        // Upcoming tracks
        if (normalizedQuery.contains("track")) {
            val tracks = loadArray(R.raw.upcoming_tracks)
            val names = (0 until tracks.length()).map { i ->
                tracks.getJSONObject(i).optString("name")
            }
            return "Upcoming tracks: ${names.joinToString(", ")}"
        }

        // Default → nothing found
        return ""
    }

    // --- Utility loaders ---
    private fun loadJson(fileId: Int): JSONObject {
        val inputStream = context.resources.openRawResource(fileId)
        val jsonText = inputStream.bufferedReader().use { it.readText() }
        return JSONObject(jsonText)
    }

    private fun loadArray(fileId: Int): JSONArray {
        val inputStream = context.resources.openRawResource(fileId)
        val jsonText = inputStream.bufferedReader().use { it.readText() }
        return JSONObject(jsonText).optJSONArray("data")
            ?: JSONObject(jsonText).optJSONArray("upcoming_tracks")
            ?: JSONArray()
    }
}
