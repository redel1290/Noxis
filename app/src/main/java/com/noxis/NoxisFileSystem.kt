package com.noxis

import android.content.Context
import android.os.Environment
import java.io.File

object NoxisFileSystem {

    // Шляхи
    lateinit var systemDir: File      // /data/data/com.noxis/files/system/
    lateinit var appsDir: File        // /data/data/com.noxis/files/apps/
    lateinit var userDir: File        // /0/Noxis/
    lateinit var desktopDir: File     // /0/Noxis/Desktop/
    lateinit var documentsDir: File   // /0/Noxis/Documents/
    lateinit var picturesDir: File    // /0/Noxis/Pictures/
    lateinit var musicDir: File       // /0/Noxis/Music/
    lateinit var videosDir: File      // /0/Noxis/Videos/
    lateinit var backupDir: File      // /0/Android/data/com.noxis/backup/

    fun init(context: Context) {
        val internalFiles = context.filesDir
        val externalRoot = Environment.getExternalStorageDirectory()
        val externalAndroid = context.getExternalFilesDir(null)

        // Внутрішні (системні)
        systemDir = File(internalFiles, "system").also { it.mkdirs() }
        appsDir = File(internalFiles, "apps").also { it.mkdirs() }

        // Зовнішні (користувач)
        val noxisRoot = File(externalRoot, "Noxis")
        userDir = noxisRoot.also { it.mkdirs() }
        desktopDir = File(noxisRoot, "Desktop").also { it.mkdirs() }
        documentsDir = File(noxisRoot, "Documents").also { it.mkdirs() }
        picturesDir = File(noxisRoot, "Pictures").also { it.mkdirs() }
        musicDir = File(noxisRoot, "Music").also { it.mkdirs() }
        videosDir = File(noxisRoot, "Videos").also { it.mkdirs() }

        // Бекап
        backupDir = File(externalAndroid?.parentFile, "backup").also { it.mkdirs() }

        // Перевірка чи потрібно відновлення (якщо папка Noxis порожня але є бекап)
        checkRestoreNeeded()
    }

    private fun checkRestoreNeeded() {
        val hasUserData = userDir.listFiles()?.isNotEmpty() == true
        val hasBackup = backupDir.listFiles()?.isNotEmpty() == true

        if (!hasUserData && hasBackup) {
            // Буде оброблено в UI — показати діалог відновлення
            needsRestore = true
        }
    }

    var needsRestore: Boolean = false

    fun createBackup() {
        // Видалити старий бекап
        backupDir.listFiles()?.forEach { it.deleteRecursively() }

        // Скопіювати поточний стан
        userDir.listFiles()?.forEach { file ->
            file.copyRecursively(File(backupDir, file.name), overwrite = true)
        }
    }

    fun restoreFromBackup() {
        backupDir.listFiles()?.forEach { file ->
            file.copyRecursively(File(userDir, file.name), overwrite = true)
        }
        needsRestore = false
    }
}
