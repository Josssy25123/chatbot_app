// File: app/src/main/java/com/example/chatbotapp/screens/OtherScreens.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.chatbotapp.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.chatbotapp.auth.AuthService
import com.example.chatbotapp.auth.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// --------------------------------------
// MSU Course Data and CurriculumContent()
// --------------------------------------

data class MSUCourse(
    val courseCode: String,
    val courseName: String,
    val credits: Int,
    val prerequisites: List<String>,
    val offered: List<String>,
    val category: String = "Core"
)

@Composable
fun CurriculumContent(modifier: Modifier = Modifier, onLaunchBrowser: (String) -> Unit) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var expandedCourse by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All", "Math", "Core CS", "Advanced", "Electives", "Capstone")

    val msuCourses = remember {
        listOf(
            MSUCourse("MATH 241", "Calculus I", 4, listOf("ENGR 101", "MATH 114", "MATH 141"), listOf("Fall", "Spring"), "Math"),
            MSUCourse("COSC 111", "Intro to Computer Science I", 4, emptyList(), listOf("Fall", "Spring"), "Core CS")
        )
    }

    val filteredCourses = msuCourses.filter { course ->
        val matchesCategory = selectedCategory == "All" || course.category == selectedCategory
        val matchesSearch = searchQuery.isEmpty() ||
                course.courseCode.contains(searchQuery, true) ||
                course.courseName.contains(searchQuery, true)
        matchesCategory && matchesSearch
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search courses...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    selected = selectedCategory == category,
                    leadingIcon = if (selectedCategory == category) {
                        { Icon(Icons.Filled.Check, contentDescription = null) }
                    } else null
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onLaunchBrowser("https://morgan.edu/gateway") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Public, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Access Degreeworks Gateway")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredCourses) { course ->
                MSUCourseCard(course, expandedCourse == course.courseCode) {
                    expandedCourse = if (expandedCourse == course.courseCode) null else course.courseCode
                }
            }
        }
    }
}

@Composable
fun MSUCourseCard(course: MSUCourse, isExpanded: Boolean, onToggleExpand: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onToggleExpand
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(course.courseCode, fontWeight = FontWeight.Bold)
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }
            Text(course.courseName)
            if (isExpanded) {
                Spacer(Modifier.height(8.dp))
                Text("Prerequisites: ${course.prerequisites.joinToString().ifEmpty { "None" }}")
                Text("Offered: ${course.offered.joinToString()}")
            }
        }
    }
}

// --------------------------------------
// Profile Screen and Utilities
// --------------------------------------

@Composable
fun ProfileContent(modifier: Modifier = Modifier, onLogout: () -> Unit = {}) {
    val authService = remember { AuthService() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val storage = remember { FirebaseStorage.getInstance() }
    val scope = rememberCoroutineScope()

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { userProfile = authService.getUserProfile() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // ✅ Use Person icon instead of Image
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { showEditDialog = true },
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(userProfile?.fullName ?: "User", fontWeight = FontWeight.Bold)
                Text(userProfile?.email ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(12.dp))

        val settings = listOf(
            Triple("Edit Profile", Icons.Filled.Edit) { showEditDialog = true },
            Triple("Export Chats", Icons.Filled.FileDownload) { scope.launch { exportChatData(firestore) } },
            Triple("Clear All Chats", Icons.Filled.DeleteSweep) { scope.launch { clearAllChats(firestore) } },
            Triple("Delete Account", Icons.Filled.DeleteForever) { showDeleteDialog = true },
            Triple("Sign Out", Icons.Filled.ExitToApp) { showLogoutDialog = true }
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(settings) { (title, icon, action) ->
                SettingsCard(title = title, icon = icon, onClick = action)
            }
        }
    }

    // ✅ Edit profile dialog
    if (showEditDialog) {
        EditProfileDialog(
            userProfile = userProfile,
            onDismiss = { showEditDialog = false },
            onSave = { updatedProfile ->
                userProfile = updatedProfile
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                scope.launch {
                    deleteAccount(authService, firestore, storage)
                    onLogout()
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = {
                    authService.signOut()
                    showLogoutDialog = false
                    onLogout()
                }) { Text("Sign Out", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun EditProfileDialog(
    userProfile: UserProfile?,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(userProfile?.fullName ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(UserProfile(fullName = name, email = userProfile?.email ?: "", profileImageUrl = userProfile?.profileImageUrl ?: ""))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(title, Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to permanently delete your account?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Delete") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// Firebase helpers
suspend fun exportChatData(firestore: FirebaseFirestore) { println("Export chat data") }
suspend fun clearAllChats(firestore: FirebaseFirestore) { println("Clear chats") }
suspend fun deleteAccount(auth: AuthService, firestore: FirebaseFirestore, storage: FirebaseStorage) {
    println("Delete user account")
}