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
    val passwordHash by prefs.passwordHash.collectAsState(initial = null)
    var input by remember { mutableStateOf("") }
    var confirmInput by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    // null = ще не завантажилось, "" = пароль не встановлено
    val isFirst = passwordHash == ""
    val isLoading = passwordHash == null

    if (isLoading) return

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460)))
        ),
        contentAlignment = Alignment.Center
    ) {
        // Горизонтальний layout для landscape
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ліва частина — аватар + ім'я
            Column(
                modifier = Modifier.width(160.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier.size(64.dp).clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
                Text(
                    if (isFirst) "Noxis" else username,
                    color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium
                )
                if (isFirst) Text(
                    "Перший запуск\nВстанови пароль",
                    color = Color.White.copy(0.6f), fontSize = 11.sp
                )
            }

            Spacer(Modifier.width(32.dp))

            // Права частина — поля вводу
            Column(
                modifier = Modifier.width(280.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Поле імені (тільки при першому запуску)
                if (isFirst) {
                    var nameInput by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Ім'я користувача", color = Color.White.copy(0.7f)) },
                        singleLine = true,
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.White.copy(0.6f)) }
                    )
                    LaunchedEffect(nameInput) {
                        if (nameInput.isNotEmpty()) prefs.setUsername(nameInput)
                    }
                }

                // Поле пароля
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it; error = "" },
                    label = { Text(if (isFirst) "Новий пароль" else "Пароль", color = Color.White.copy(0.7f)) },
                    visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPw = !showPw }) {
                            Icon(
                                if (showPw) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null, tint = Color.White.copy(0.6f)
                            )
                        }
                    },
                    singleLine = true,
                    isError = error.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        scope.launch { tryLogin(isFirst, input, confirmInput, passwordHash ?: "", prefs, onUnlock) { error = it } }
                    }),
                    colors = fieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Підтвердження пароля (тільки перший запуск)
                if (isFirst) {
                    OutlinedTextField(
                        value = confirmInput,
                        onValueChange = { confirmInput = it; error = "" },
                        label = { Text("Підтвердити пароль", color = Color.White.copy(0.7f)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = error.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        colors = fieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Помилка
                if (error.isNotEmpty()) {
                    Text(error, color = Color(0xFFFF6B6B), fontSize = 12.sp)
                }

                // Кнопка
                Button(
                    onClick = {
                        scope.launch { tryLogin(isFirst, input, confirmInput, passwordHash ?: "", prefs, onUnlock) { error = it } }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (isFirst) "Встановити та увійти" else "Увійти",
                        color = Color.White, fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

suspend fun tryLogin(
    isFirst: Boolean,
    input: String,
    confirm: String,
    hash: String,
    prefs: NoxisPreferences,
    onUnlock: () -> Unit,
    onError: (String) -> Unit
) {
    if (input.isEmpty()) { onError("Введи пароль"); return }
    if (isFirst) {
        if (input != confirm) { onError("Паролі не збігаються"); return }
        if (input.length < 4) { onError("Мінімум 4 символи"); return }
        prefs.setPasswordHash(prefs.hashPassword(input))
        onUnlock()
    } else {
        if (prefs.hashPassword(input) == hash) onUnlock()
        else onError("Невірний пароль")
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color.White.copy(0.8f),
    unfocusedBorderColor = Color.White.copy(0.3f),
    errorBorderColor = Color(0xFFFF6B6B),
    cursorColor = Color.White,
    focusedLabelColor = Color.White.copy(0.8f),
    unfocusedLabelColor = Color.White.copy(0.5f)
)
