package com.noxis

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.noxis.data.NoxisPreferences
import com.noxis.ui.screens.DesktopScreen
import com.noxis.ui.screens.LockScreen
import com.noxis.ui.theme.NoxisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Повноекранний режим
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val prefs = NoxisPreferences(this)
        setContent {
            val isDark by prefs.isDarkTheme.collectAsState(initial = false)
            NoxisTheme(darkTheme = isDark) {
                var unlocked by remember { mutableStateOf(false) }
                if (!unlocked) LockScreen(prefs = prefs, onUnlock = { unlocked = true })
                else DesktopScreen(prefs = prefs)
            }
        }
    }
}
