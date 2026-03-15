package com.noxis.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp

@Composable
fun NotepadScreen() {
    var text by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = {}) { Text("Файл", fontSize = androidx.compose.ui.unit.TextUnit.Unspecified) }
            TextButton(onClick = {}) { Text("Правка") }
        }
        HorizontalDivider()
        TextField(
            value = text, onValueChange = { text = it },
            modifier = Modifier.fillMaxSize(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
            ),
            placeholder = { Text("Почни писати...") }
        )
    }
}
