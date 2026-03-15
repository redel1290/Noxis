package com.noxis.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileManagerScreen(startPath: String = "/storage/emulated/0/Noxis") {
    var currentPath by remember { mutableStateOf(startPath) }
    val files = remember(currentPath) {
        File(currentPath).listFiles()
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
            ?: emptyList()
    }
    var ctxFile by remember { mutableStateOf<File?>(null) }

    Column(Modifier.fillMaxSize()) {
        // Шлях
        Row(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentPath != startPath) {
                IconButton(onClick = { currentPath = File(currentPath).parent ?: startPath }) {
                    Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(18.dp))
                }
            }
            Icon(Icons.Default.Folder, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(currentPath.replace("/storage/emulated/0", "~"), fontSize = 12.sp, maxLines = 1)
        }
        HorizontalDivider()

        if (files.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Папка порожня", color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(files) { file ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .combinedClickable(
                                onClick = { if (file.isDirectory) currentPath = file.absolutePath },
                                onLongClick = { ctxFile = file }
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                            null,
                            tint = if (file.isDirectory) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurface.copy(0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Column(Modifier.weight(1f)) {
                            Text(file.name, fontSize = 13.sp, maxLines = 1)
                            Text(
                                if (file.isDirectory) "Папка" else formatSize(file.length()),
                                fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                            )
                        }
                        Text(
                            SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(Date(file.lastModified())),
                            fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.3f))
                }
            }
        }
    }

    ctxFile?.let { file ->
        DropdownMenu(expanded = true, onDismissRequest = { ctxFile = null }) {
            DropdownMenuItem(
                leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, null) },
                text = { Text("Перейменувати") },
                onClick = { ctxFile = null }
            )
            DropdownMenuItem(
                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color(0xFFFF5252)) },
                text = { Text("Видалити", color = Color(0xFFFF5252)) },
                onClick = { file.delete(); ctxFile = null }
            )
        }
    }
}

fun formatSize(bytes: Long): String = when {
    bytes < 1024 -> "$bytes Б"
    bytes < 1024 * 1024 -> "${bytes / 1024} КБ"
    else -> "${bytes / (1024 * 1024)} МБ"
}
