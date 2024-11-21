package com.example.smartpest

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.smartpest.viewmodels.AuthState
import com.example.smartpest.viewmodels.AuthViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfilePage(navController: NavHostController, authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("English") }

    val languages = listOf("English", "Spanish", "French", "German", "Hindi")
    var isChanged by remember { mutableStateOf(false) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.PasswordResetEmailSent -> {
                Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
            }

            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT)
                    .show()
            }

            else -> Unit
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    isChanged = true
                    name = it
                },
                label = { Text("Name") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Location Field
            OutlinedTextField(
                value = location,
                onValueChange = {
                    isChanged = true
                    location = it
                },
                label = { Text("Location") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Phone Field
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    isChanged = true
                    phone = it
                },
                label = { Text("Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Language Dropdown
            DropdownMenuField(
                selectedItem = selectedLanguage,
                items = languages,
                onItemSelected = {
                    isChanged = true
                    selectedLanguage = it
                }
            )

            // Save Button
            Button(
                onClick = {
                    // Handle save logic here
                    Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
                    isChanged = false
                },
                enabled = isChanged,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                Text("Save")
            }

            // Reset Password Button
            Button(
                onClick = {
                    showForgotPasswordDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                Text("Reset Password")
            }

            // Privacy Policy
            TextButton(
                onClick = {
                    navController.navigate("")
                }
            ) {
                Text("Privacy Policy")
            }

            if (showForgotPasswordDialog) {
                ForgotPasswordDialog(
                    onDismiss = { showForgotPasswordDialog = false },
                    onResetPassword = { email ->
                        authViewModel.resetPassword(email)
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuField(
    selectedItem: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldWidth = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text("Language") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldWidth.value = coordinates.size.width
                }
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldWidth.value.toDp() })
        ) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    onItemSelected(item)
                    expanded = false
                }) {
                    Text(item)
                }
            }
        }
    }
}
