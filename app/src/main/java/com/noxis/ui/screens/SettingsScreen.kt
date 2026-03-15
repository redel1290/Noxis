package com.noxis.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.noxis.data.NoxisPreferences
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(prefs: NoxisPreferences) {
    val scope = rememberCoroutineScope()
    val isDark by prefs.isDarkTheme.collectAsState(initial = false)
    val username by prefs.username.collectAsState(initial = "User")
    var nameInput by remember(username) { mutableStateOf(username) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Налаштування", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider()

        // Тема
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DarkMode, null)
                Text("Темна тема")
            }
            Switch(checked = isDark, onCheckedChange = { scope.launch { prefs.setDarkTheme(it) } })
        }

        HorizontalDivider()

        // Ім'я
        Text("Ім'я користувача", style = MaterialTheme.typography.labelMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, modifier = Modifier.weight(1f), singleLine = true)
            Button(onClick = { scope.launch { prefs.setUsername(nameInput) } }) { Text("Зберегти") }
        }

        HorizontalDivider()

        // Про систему
        Text("Про Noxis", style = MaterialTheme.typography.labelMedium)
        Text("Версія: 1.0.0", style = MaterialTheme.typography.bodySmall)
        Text("Платформа: Android", style = MaterialTheme.typography.bodySmall)
    }
}
