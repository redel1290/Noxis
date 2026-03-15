package com.noxis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotepadScreen() {
    var text by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Файл", "Правка", "Вид").forEach { menu ->
                TextButton(onClick = {}, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                    Text(menu, fontSize = 11.sp)
                }
            }
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
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
            placeholder = { Text("Почни писати...", fontSize = 12.sp) }
        )
    }
}
