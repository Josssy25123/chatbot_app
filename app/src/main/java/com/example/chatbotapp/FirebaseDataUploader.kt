package com.example.chatbotapp

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.IOException

class FirebaseDataUploader(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirebaseUploader"

    // List of your JSON file names (without .json extension)
    private val jsonFiles = listOf(
        "academic_resources",
        "advising",
        "career_educational_resource",
        "classes",
        "degree",
        "department",
        "library",
        "morgan_state_calendar",
        "registration",
        "upcoming_tracks"
    )

    /**
     * Upload with better structure - nested data preserved
     */
    fun uploadWithNestedStructure() {
        Log.d(TAG, "Starting structured upload...")

        jsonFiles.forEach { fileName ->
            try {
                val jsonString = loadJSONFromRaw(fileName)
                if (jsonString == null) {
                    Log.e(TAG, "Failed to load $fileName.json")
                    return@forEach
                }

                val jsonObject = JSONObject(jsonString)
                Log.d(TAG, "Processing $fileName.json...")

                // Convert JSON to Map for Firestore
                val dataMap = jsonObjectToMap(jsonObject)

                // Create document with file name as document ID
                db.collection("knowledge_base")
                    .document(fileName)
                    .set(dataMap)
                    .addOnSuccessListener {
                        Log.d(TAG, "✓ Uploaded $fileName.json as structured document")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "✗ Error uploading $fileName.json", e)
                    }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing $fileName.json", e)
            }
        }
    }

    /**
     * Load JSON file from raw folder
     */
    private fun loadJSONFromRaw(fileName: String): String? {
        return try {
            // Get resource ID from raw folder
            val resourceId = context.resources.getIdentifier(
                fileName,
                "raw",
                context.packageName
            )

            if (resourceId == 0) {
                Log.e(TAG, "Resource not found: $fileName")
                return null
            }

            val inputStream = context.resources.openRawResource(resourceId)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            Log.e(TAG, "Error loading JSON file: $fileName", ex)
            null
        }
    }

    /**
     * Convert JSONObject to Map recursively
     */
    private fun jsonObjectToMap(json: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = json.keys()

        while (keys.hasNext()) {
            val key = keys.next()
            var value = json.get(key)

            when (value) {
                is JSONObject -> {
                    value = jsonObjectToMap(value)
                }
                is org.json.JSONArray -> {
                    value = jsonArrayToList(value)
                }
            }
            map[key] = value
        }
        return map
    }

    /**
     * Convert JSONArray to List recursively
     */
    private fun jsonArrayToList(array: org.json.JSONArray): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until array.length()) {
            var value = array.get(i)
            when (value) {
                is JSONObject -> {
                    value = jsonObjectToMap(value)
                }
                is org.json.JSONArray -> {
                    value = jsonArrayToList(value)
                }
            }
            list.add(value)
        }
        return list
    }
}