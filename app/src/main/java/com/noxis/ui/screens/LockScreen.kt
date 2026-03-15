package com.noxis.ui.screens

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
    var nameInput by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val isLoading = passwordHash == null
    val isFirst = passwordHash == ""
    if (isLoading) return

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF0078D4), Color(0xFF005A9E), Color(0xFF003A6E)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар + ім'я
            Column(
                modifier = Modifier.width(120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                Text(if (isFirst) "Noxis" else username, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                if (isFirst) Text("Перший запуск", color = Color.White.copy(0.7f), fontSize = 10.sp)
            }

            Spacer(Modifier.width(40.dp))

            // Форма
            Column(
                modifier = Modifier.width(260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isFirst) {
                    WinTextField(value = nameInput, onValueChange = { nameInput = it }, label = "Ім'я користувача")
                }
                WinTextField(
                    value = input, onValueChange = { input = it; error = "" },
                    label = if (isFirst) "Пароль" else "PIN або пароль",
                    isPassword = true, showPassword = showPw,
                    onTogglePassword = { showPw = !showPw },
                    onDone = { scope.launch { doLogin(isFirst, input, confirmInput, nameInput, passwordHash ?: "", prefs, onUnlock) { error = it } } }
                )
                if (isFirst) {
                    WinTextField(
                        value = confirmInput, onValueChange = { confirmInput = it; error = "" },
                        label = "Підтвердити пароль", isPassword = true, showPassword = showPw,
                        onDone = { scope.launch { doLogin(isFirst, input, confirmInput, nameInput, passwordHash ?: "", prefs, onUnlock) { error = it } } }
                    )
                }
                if (error.isNotEmpty()) Text(error, color = Color(0xFFFF6B6B), fontSize = 11.sp)
                Button(
                    onClick = { scope.launch { doLogin(isFirst, input, confirmInput, nameInput, passwordHash ?: "", prefs, onUnlock) { error = it } } },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.25f)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(Icons.Default.ArrowForward, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isFirst) "Створити" else "Увійти", color = Color.White, fontSize = 13.sp)
                }
            }
        }

        // Час знизу
        Text(
            getCurrentTimeAndDate(),
            color = Color.White.copy(0.8f), fontSize = 11.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
        )
    }
}

@Composable
fun WinTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    isPassword: Boolean = false, showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null, onDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        singleLine = true,
        visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword && onTogglePassword != null) {{
            IconButton(onClick = onTogglePassword, modifier = Modifier.size(32.dp)) {
                Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null,
                    modifier = Modifier.size(16.dp), tint = Color.White.copy(0.7f))
            }
        }} else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White.copy(0.9f), unfocusedBorderColor = Color.White.copy(0.4f),
            cursorColor = Color.White, focusedLabelColor = Color.White.copy(0.9f),
            unfocusedLabelColor = Color.White.copy(0.5f)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    )
}

suspend fun doLogin(
    isFirst: Boolean, input: String, confirm: String, name: String,
    hash: String, prefs: NoxisPreferences, onUnlock: () -> Unit, onError: (String) -> Unit
) {
    if (input.isEmpty()) { onError("Введи пароль"); return }
    if (isFirst) {
        if (input.length < 4) { onError("Мінімум 4 символи"); return }
        if (input != confirm) { onError("Паролі не збігаються"); return }
        if (name.isNotEmpty()) prefs.setUsername(name)
        prefs.setPasswordHash(prefs.hashPassword(input))
        onUnlock()
    } else {
        if (prefs.hashPassword(input) == hash) onUnlock()
        else onError("Невірний пароль")
    }
}

fun getCurrentTimeAndDate(): String {
    val sdf = java.text.SimpleDateFormat("EEEE, d MMMM yyyy  HH:mm", java.util.Locale("uk"))
    return sdf.format(java.util.Date())
}
