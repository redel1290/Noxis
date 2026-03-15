package com.noxis.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.noxis.data.NoxisPreferences
import kotlinx.coroutines.launch

@Composable
fun LockScreen(prefs: NoxisPreferences, onUnlock: () -> Unit) {
    val scope = rememberCoroutineScope()
    val username by prefs.username.collectAsState(initial = "User")
    val passwordHash by prefs.passwordHash.collectAsState(initial = "")
    var input by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var isFirst by remember { mutableStateOf(false) }
    var confirmInput by remember { mutableStateOf("") }

    // Якщо пароль не встановлений — перший запуск
    LaunchedEffect(passwordHash) {
        if (passwordHash.isEmpty()) isFirst = true
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(320.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.1f))
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    Modifier.size(72.dp).clip(RoundedCornerShape(36.dp))
                        .background(Color.White.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(44.dp))
                }

                Text(
                    if (isFirst) "Ласкаво просимо!" else username,
                    color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium
                )

                if (isFirst) Text(
                    "Встанови пароль для входу",
                    color = Color.White.copy(0.7f), fontSize = 13.sp
                )

                OutlinedTextField(
                    value = input, onValueChange = { input = it; error = false },
                    label = { Text(if (isFirst) "Новий пароль" else "Пароль", color = Color.White.copy(0.7f)) },
                    visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPw = !showPw }) {
                            Icon(if (showPw) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Color.White.copy(0.7f))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { /* handled by button */ }),
                    isError = error,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White.copy(0.8f), unfocusedBorderColor = Color.White.copy(0.3f),
                        errorBorderColor = Color(0xFFFF6B6B), cursorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isFirst) {
                    OutlinedTextField(
                        value = confirmInput, onValueChange = { confirmInput = it; error = false },
                        label = { Text("Підтверди пароль", color = Color.White.copy(0.7f)) },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = error,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White.copy(0.8f), unfocusedBorderColor = Color.White.copy(0.3f),
                            errorBorderColor = Color(0xFFFF6B6B), cursorColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AnimatedVisibility(error) {
                    Text(
                        if (isFirst) "Паролі не збігаються" else "Невірний пароль",
                        color = Color(0xFFFF6B6B), fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        scope.launch {
                            if (isFirst) {
                                if (input.isNotEmpty() && input == confirmInput) {
                                    prefs.setPasswordHash(prefs.hashPassword(input))
                                    onUnlock()
                                } else error = true
                            } else {
                                if (prefs.hashPassword(input) == passwordHash) onUnlock()
                                else { error = true; input = "" }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f))
                ) {
                    Text(if (isFirst) "Встановити пароль" else "Увійти", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
