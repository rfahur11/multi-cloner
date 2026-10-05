package com.porto.multicloner.core.storage

import android.content.Context
import java.io.File

object VFileSystem {

    /**
     * Mengembalikan root directory penyimpanan virtual host
     * Path: /data/data/com.porto.multicloner/files/virtual/
     */
    fun getVirtualRoot(context: Context): File {
        val root = File(context.filesDir, "virtual")
        if (!root.exists()) root.mkdirs()
        return root
    }

    /**
     * Mengembalikan folder data spesifik per user space dan package name
     * Contoh: /data/data/com.porto.multicloner/files/virtual/users/1/com.whatsapp/
     */
    fun getUserDataDir(context: Context, userId: Int, packageName: String): File {
        val userDir = File(getVirtualRoot(context), "users/$userId/$packageName")
        if (!userDir.exists()) {
            userDir.mkdirs()
            // Inisialisasi sub-folder standar Android
            File(userDir, "databases").mkdirs()
            File(userDir, "shared_prefs").mkdirs()
            File(userDir, "cache").mkdirs()
            File(userDir, "files").mkdirs()
        }
        return userDir
    }

    /**
     * Menghapus seluruh data direktori akun klon tertentu saat di-reset / di-uninstall
     */
    fun deleteUserDataDir(context: Context, userId: Int, packageName: String): Boolean {
        val userDir = File(getVirtualRoot(context), "users/$userId/$packageName")
        return if (userDir.exists()) {
            userDir.deleteRecursively()
        } else {
            true
        }
    }
}
