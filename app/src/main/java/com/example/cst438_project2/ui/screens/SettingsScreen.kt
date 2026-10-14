
package com.example.cst438_project2.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val SettingsBackground = Color(0xFF121214)
private val SettingsSurface = Color(0xFF1C1C20)
private val SettingsPurple = Color(0xFF5A287D)
private val SettingsDanger = Color(0xFFB3261E)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isAdmin: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Temporary UI state. These values are not saved to the backend.
    var username by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }
    // Temporary UI state for admin actions.
    var targetUsername by remember { mutableStateOf("") }
    var targetComment by remember { mutableStateOf("") }
    var pendingAdminAction by remember { mutableStateOf<String?>(null) }

    // Opens the device's image picker for the profile icon.
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            profileImageUri = uri.toString()
        }
    }

    // Confirms before the UI-only delete action.
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SettingsBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile icon
            SettingsSection(title = "Profile Icon") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profile icon preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default profile icon",
                            tint = Color.LightGray,
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            imagePicker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Change Icon")
                    }
                }
            }

            // Username
            SettingsSection(title = "Change Username") {
                SettingsTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "New Username",
                    placeholder = "Enter a username"
                )

                SettingsActionButton(
                    text = "Save Username",
                    onClick = {
                        // TODO: Connect to the username update API.
                    }
                )
            }

            // Password
            SettingsSection(title = "Change Password") {
                SettingsTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Current Password",
                    placeholder = "Enter current password",
                    isPassword = true
                )

                SettingsTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New Password",
                    placeholder = "Enter new password",
                    isPassword = true
                )

                SettingsActionButton(
                    text = "Update Password",
                    onClick = {
                        // TODO: Connect to password update logic if supported.
                    }
                )
            }


            if (isAdmin) {
                // ADMIN FEATURE 1: Delete a user's comment.
                SettingsSection(title = "Delete User Comment") {
                    Text(
                        text = "Remove an inappropriate or unwanted comment.",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    SettingsTextField(
                        value = targetUsername,
                        onValueChange = { targetUsername = it },
                        label = "Username",
                        placeholder = "Enter the comment author's username"
                    )

                    SettingsTextField(
                        value = targetComment,
                        onValueChange = { targetComment = it },
                        label = "Comment",
                        placeholder = "Identify the comment to remove"
                    )

                    Button(
                        onClick = {
                            pendingAdminAction = "comment"
                        },
                        enabled = targetUsername.isNotBlank() &&
                                targetComment.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SettingsDanger,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Delete User Comment")
                    }
                }

                // ADMIN FEATURE 2: Delete a user account.
                SettingsSection(title = "Delete User") {
                    Text(
                        text = "Remove a user's account. This is separate " +
                                "from deleting your own account.",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    SettingsTextField(
                        value = targetUsername,
                        onValueChange = { targetUsername = it },
                        label = "Username",
                        placeholder = "Enter the username to remove"
                    )

                    Button(
                        onClick = {
                            pendingAdminAction = "user"
                        },
                        enabled = targetUsername.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SettingsDanger,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Delete User")
                    }
                }
            }


            // Log out
            SettingsSection(title = "Session") {
                Text(
                    text = "Log out of your FavGame account on this device.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                SettingsActionButton(
                    text = "Log Out",
                    onClick = onLogoutClick
                )
            }

            // Delete account
            SettingsSection(title = "Delete Account") {
                Text(
                    text = "This action is intended to permanently delete your account.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Button(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsDanger,
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete Account")
                }
            }
        }
    }

    // UI confirmation only. No account is deleted yet.
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Account?") },
            text = {
                Text(
                    "Account deletion is not connected yet. " +
                            "No account will be deleted."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Got it")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = SettingsSurface,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }


    if (pendingAdminAction != null) {
        val actionName = if (pendingAdminAction == "comment") {
            "Delete User Comment"
        } else {
            "Delete User"
        }

        AlertDialog(
            onDismissRequest = {
                pendingAdminAction = null
            },
            title = {
                Text(actionName)
            },
            text = {
                Text(
                    "This is a UI preview. No comments or user accounts " +
                            "will be deleted until the admin functionality is connected."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Connect the appropriate admin API.
                        // No deletion happens in this UI-only version.
                        pendingAdminAction = null
                    }
                ) {
                    Text("Got it")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingAdminAction = null
                    }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = SettingsSurface,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }

}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SettingsSurface,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        content()
    }
}

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            androidx.compose.ui.text.input.VisualTransformation.None
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = SettingsPurple,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.LightGray,
            cursorColor = Color.White
        )
    )
}

@Composable
private fun SettingsActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SettingsPurple,
            contentColor = Color.White
        )
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}
