package com.noxis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.noxis.ui.theme.NoxisTheme
import com.noxis.ui.screens.LockScreen
import com.noxis.ui.screens.DesktopScreen
import com.noxis.data.NoxisPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ініціалізація папок Noxis
        NoxisFileSystem.init(this)

        setContent {
            val prefs = remember { NoxisPreferences(this) }
            val isDark by prefs.isDarkTheme.collectAsState(initial = false)

            NoxisTheme(darkTheme = isDark) {
                NoxisApp(prefs = prefs)
            }
        }
    }
}

@Composable
fun NoxisApp(prefs: NoxisPreferences) {
    var isUnlocked by remember { mutableStateOf(false) }

    if (!isUnlocked) {
        LockScreen(
            prefs = prefs,
            onUnlock = { isUnlocked = true }
        )
    } else {
        DesktopScreen(prefs = prefs)
    }
}
