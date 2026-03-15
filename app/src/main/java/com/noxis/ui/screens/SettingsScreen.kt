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
import androidx.compose.ui.unit.sp
import com.noxis.data.NoxisPreferences
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(prefs: NoxisPreferences) {
    val scope = rememberCoroutineScope()
    val isDark by prefs.isDarkTheme.collectAsState(initial = false)
    val username by prefs.username.collectAsState(initial = "User")
    var nameInput by remember(username) { mutableStateOf(username) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Налаштування", style = MaterialTheme.typography.titleSmall)
        HorizontalDivider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Темна тема", fontSize = 12.sp)
            Switch(checked = isDark, onCheckedChange = { scope.launch { prefs.setDarkTheme(it) } })
        }
        HorizontalDivider()
        Text("Ім'я користувача", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, modifier = Modifier.weight(1f), singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp))
            Button(onClick = { scope.launch { prefs.setUsername(nameInput) } }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                Text("OK", fontSize = 11.sp)
            }
        }
        HorizontalDivider()
        Text("Noxis v1.0.0  •  Android", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
    }
}
