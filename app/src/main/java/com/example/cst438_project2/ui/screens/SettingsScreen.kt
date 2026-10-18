package com.example.cst438_project2.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import com.example.cst438_project2.data.model.User
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
    currentUserId: String? = null,
    adminUsers: List<User> = emptyList(),
    adminLoading: Boolean = false,
    adminError: String? = null,
    onLoadAdminUsers: () -> Unit = {},
    onDeleteAdminUser: (String) -> Unit = {},
    onToggleAdminUser: (String, Boolean) -> Unit = { _, _ -> },
    displayName: String = "",
    profileImageUrl: String? = null,
    onSaveIcon: (String) -> Unit = {},
    loading: Boolean = false,
    error: String? = null,
    deleteSucceeded: Boolean = false,
    onSaveDisplayName: (String) -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onClearError: () -> Unit = {},
    onAccountDeleted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var username by remember(displayName) {
        mutableStateOf(displayName)
    }

    var profileImageUri by remember(profileImageUrl) {
        mutableStateOf(profileImageUrl)
    }

    // Temporary UI state for admin actions.
    var userPendingRoleChange by remember {
        mutableStateOf<User?>(null)
    }

    var userPendingDelete by remember {
        mutableStateOf<User?>(null)
    }

    // Opens the device's image picker for the profile icon.
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            profileImageUri = uri.toString()
        }
    }

    // Confirms before the UI-only delete action.
    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(deleteSucceeded) {
        if (deleteSucceeded) {
            onAccountDeleted()
        }
    }

    LaunchedEffect(isAdmin) {
        if (isAdmin) {
            onLoadAdminUsers()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SettingsBackground)
            .padding(horizontal = 6.dp, vertical = 4.dp)
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
            if (loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (error != null) {
                Text(
                    text = error,
                    color = Color(0xFFFF8A80),
                    fontSize = 14.sp
                )
            }

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

                    SettingsActionButton(
                        text = "Save Profile Icon",
                        enabled = !loading &&
                                !profileImageUri.isNullOrBlank(),
                        onClick = {
                            profileImageUri?.let { uri ->
                                onClearError()
                                onSaveIcon(uri)
                            }
                        }
                    )
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
                    enabled = !loading &&
                            username.isNotBlank(),
                    onClick = {
                        val newName = username.trim()

                        if (newName.isNotEmpty()) {
                            onClearError()
                            onSaveDisplayName(newName)
                        }
                    }
                )
            }


            if (isAdmin) {
                // ADMIN FEATURE: View and manage all users.
                SettingsSection(
                    title = "Users (${adminUsers.size})"
                ) {
                    if (adminLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (adminError != null) {
                        Text(
                            text = adminError,
                            color = Color(0xFFFF8A80),
                            fontSize = 14.sp
                        )
                    }

                    when {
                        adminLoading &&
                                adminUsers.isEmpty() -> {
                            Text(
                                text = "Loading users...",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        }

                        adminUsers.isEmpty() -> {
                            Text(
                                text = "No users were found.",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 420.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = adminUsers,
                                    key = { adminUser ->
                                        adminUser.id
                                            ?: adminUser.githubId
                                    }
                                ) { adminUser ->
                                    AdminUserRow(
                                        user = adminUser,
                                        loading = adminLoading,
                                        isCurrentUser =
                                            adminUser.id == currentUserId,

                                        onRoleClick = {
                                            if (
                                                adminUser.id != currentUserId &&
                                                !adminUser.id
                                                    .isNullOrBlank()
                                            ) {
                                                userPendingRoleChange =
                                                    adminUser
                                            }
                                        },

                                        onDeleteClick = {
                                            if (
                                                adminUser.id != currentUserId &&
                                                !adminUser.id
                                                    .isNullOrBlank()
                                            ) {
                                                userPendingDelete =
                                                    adminUser
                                            }
                                        }
                                    )
                                }
                            }
                        }
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
                    onClick = {
                        showDeleteConfirmation = true
                    },
                    enabled = !loading,
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
            onDismissRequest = {
                if (!loading) {
                    showDeleteConfirmation = false
                }
            },
            title = {
                Text("Delete Account?")
            },
            text = {
                Text(
                    "This permanently deletes your FavGame account " +
                            "and associated game data. This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !loading,
                    onClick = {
                        onClearError()
                        onDeleteAccount()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete Account")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !loading,
                    onClick = {
                        showDeleteConfirmation = false
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


    // Confirmation before changing a user's role.
    userPendingRoleChange?.let { targetUser ->
        val newAdminValue = !targetUser.isAdmin

        AlertDialog(
            onDismissRequest = {
                if (!adminLoading) {
                    userPendingRoleChange = null
                }
            },
            title = {
                Text(
                    text = if (newAdminValue) {
                        "Make User Admin?"
                    } else {
                        "Make Admin a User?"
                    }
                )
            },
            text = {
                Text(
                    text = if (newAdminValue) {
                        "Are you sure you wish to make " +
                                "\"${targetUser.displayName}\" " +
                                "an admin?"
                    } else {
                        "Are you sure you wish to change " +
                                "\"${targetUser.displayName}\" " +
                                "to a regular user?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !adminLoading,
                    onClick = {
                        val userId = targetUser.id

                        if (
                            !userId.isNullOrBlank() &&
                            userId != currentUserId
                        ) {
                            onToggleAdminUser(
                                userId,
                                newAdminValue
                            )

                            userPendingRoleChange = null
                        }
                    }
                ) {
                    Text(
                        text = if (newAdminValue) {
                            "Make Admin"
                        } else {
                            "Make User"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !adminLoading,
                    onClick = {
                        userPendingRoleChange = null
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


    // Confirmation before an admin deletes a user.
    userPendingDelete?.let { targetUser ->
        AlertDialog(
            onDismissRequest = {
                if (!adminLoading) {
                    userPendingDelete = null
                }
            },
            title = {
                Text("Delete User?")
            },
            text = {
                Text(
                    text = "Permanently delete the account for " +
                            "\"${targetUser.displayName}\" and " +
                            "its associated game data? " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !adminLoading,
                    onClick = {
                        val userId = targetUser.id

                        if (
                            !userId.isNullOrBlank() &&
                            userId != currentUserId
                        ) {
                            onDeleteAdminUser(userId)
                            userPendingDelete = null
                        }
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = SettingsDanger
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !adminLoading,
                    onClick = {
                        userPendingDelete = null
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
private fun AdminUserRow(
    user: User,
    loading: Boolean,
    isCurrentUser: Boolean,
    onRoleClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SettingsBackground,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!user.iconUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.iconUrl,
                    contentDescription =
                        "${user.displayName} profile icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default profile icon",
                    tint = Color.LightGray,
                    modifier = Modifier.size(50.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = user.displayName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "GitHub ID: ${user.githubId}",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )

                Text(
                    text = if (user.isAdmin) {
                        "Admin"
                    } else {
                        "User"
                    },
                    color = if (user.isAdmin) {
                        Color(0xFFBFA0E2)
                    } else {
                        Color.LightGray
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }

        if (!isCurrentUser) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRoleClick,
                    enabled = !loading &&
                            !user.id.isNullOrBlank(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsPurple,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (user.isAdmin) {
                            "Make User"
                        } else {
                            "Make Admin"
                        },
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onDeleteClick,
                    enabled = !loading &&
                            !user.id.isNullOrBlank(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsDanger,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = "Delete",
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            Text(
                text = "Current account",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }
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
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        placeholder = {
            Text(placeholder)
        },
        singleLine = true,
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
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SettingsPurple,
            contentColor = Color.White
        )
    ) {
        Text(
            text,
            fontWeight = FontWeight.SemiBold
        )
    }
}