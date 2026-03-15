package com.noxis.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.noxis.data.NoxisPreferences
import kotlinx.coroutines.launch

@Composable
fun LockScreen(
    prefs: NoxisPreferences,
    onUnlock: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val username by prefs.username.collectAsState(initial = "User")
    val passwordHash by prefs.passwordHash.collectAsState(initial = "")

    var inputPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var shake by remember { mutableStateOf(false) }

    // Якщо пароль ще не встановлено — одразу пускаємо
    LaunchedEffect(passwordHash) {
        if (passwordHash.isEmpty()) onUnlock()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460),
                        Color(0xFF533483)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            // Ім'я користувача
            Text(
                text = username,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Поле пароля
            OutlinedTextField(
                value = inputPassword,
                onValueChange = {
                    inputPassword = it
                    error = false
                },
                placeholder = { Text("Пароль", color = Color.White.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        scope.launch {
                            val hash = prefs.hashPassword(inputPassword)
                            if (hash == passwordHash) {
                                onUnlock()
                            } else {
                                error = true
                                inputPassword = ""
                            }
                        }
                    }
                ),
                isError = error,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = 0.8f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    errorBorderColor = Color(0xFFFF6B6B),
                    cursorColor = Color.White
                ),
                modifier = Modifier.width(280.dp)
            )

            // Помилка
            AnimatedVisibility(visible = error) {
                Text(
                    text = "Невірний пароль",
                    color = Color(0xFFFF6B6B),
                    fontSize = 13.sp
                )
            }

            // Кнопка входу
            Button(
                onClick = {
                    scope.launch {
                        val hash = prefs.hashPassword(inputPassword)
                        if (hash == passwordHash) {
                            onUnlock()
                        } else {
                            error = true
                            inputPassword = ""
                        }
                    }
                },
                modifier = Modifier.width(280.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Увійти", color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}
