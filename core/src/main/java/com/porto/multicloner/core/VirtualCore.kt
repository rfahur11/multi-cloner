package com.porto.multicloner.core

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.porto.multicloner.core.daemon.DaemonService
import com.porto.multicloner.core.model.ClonedProfile
import com.porto.multicloner.core.model.DeviceIdentity
import com.porto.multicloner.core.spoof.DeviceSpoofManager
import com.porto.multicloner.core.storage.VFileSystem
import com.porto.multicloner.core.stub.StubActivity
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class VirtualCore private constructor() {

    companion object {
        private const val TAG = "VirtualCore"

        @Volatile
        private var instance: VirtualCore? = null

        fun get(): VirtualCore {
            return instance ?: synchronized(this) {
                instance ?: VirtualCore().also { instance = it }
            }
        }

        init {
            try {
                System.loadLibrary("vcore")
                Log.i(TAG, "Native vcore library loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load native vcore library", e)
            }
        }
    }

    private lateinit var appContext: Context
    private val activeProfiles = ConcurrentHashMap<String, ClonedProfile>()

    // Native C++ JNI Bindings
    private external fun nativeInitEngine(apiLevel: Int, hostPackageName: String): Boolean
    private external fun nativeGetEngineVersion(): String

    /**
     * Inisialisasi VirtualCore pada saat Application.attachBaseContext()
     */
    fun doStartup(context: Context) {
        this.appContext = context.applicationContext ?: context
        val apiLevel = Build.VERSION.SDK_INT
        val hostPkg = context.packageName

        try {
            val nativeOk = nativeInitEngine(apiLevel, hostPkg)
            Log.i(TAG, "VirtualCore Engine startup. Version: ${getEngineVersion()}, NativeOk: $nativeOk")
        } catch (t: Throwable) {
            Log.w(TAG, "Native startup warning: ${t.message}")
        }

        // Jalankan background daemon keep-alive
        DaemonService.start(context)
    }

    fun getEngineVersion(): String {
        return try {
            nativeGetEngineVersion()
        } catch (t: Throwable) {
            "1.0.0-fallback"
        }
    }

    /**
     * Membuat profil klon baru untuk aplikasi yang terinstall
     */
    fun createClone(
        packageName: String,
        aliasName: String,
        customIdentity: DeviceIdentity? = null
    ): ClonedProfile {
        val pm = appContext.packageManager
        val appInfo = pm.getApplicationInfo(packageName, 0)
        val appName = pm.getApplicationLabel(appInfo).toString()

        // Hitung slot userId berikutnya untuk package ini
        val existingClones = getClonesForPackage(packageName)
        val nextUserId = (existingClones.maxOfOrNull { it.userId } ?: -1) + 1

        val identity = customIdentity ?: DeviceSpoofManager.generateRandomIdentity()
        val profileId = "${packageName}_u$nextUserId"

        // Siapkan folder terisolasi
        VFileSystem.getUserDataDir(appContext, nextUserId, packageName)

        val profile = ClonedProfile(
            id = profileId,
            packageName = packageName,
            appName = appName,
            userId = nextUserId,
            aliasName = aliasName,
            deviceIdentity = identity
        )

        activeProfiles[profileId] = profile
        Log.i(TAG, "Cloned Profile Created: $profileId ($aliasName) with Android ID: ${identity.androidId}")
        return profile
    }

    /**
     * Mendapatkan semua klon yang aktif di memori
     */
    fun getAllClones(): List<ClonedProfile> {
        return activeProfiles.values.toList().sortedByDescending { it.createdAt }
    }

    fun getClonesForPackage(packageName: String): List<ClonedProfile> {
        return activeProfiles.values.filter { it.packageName == packageName }
    }

    /**
     * Menjalankan aplikasi yang sudah dikloning di dalam proses terisolasi
     */
    fun launchApp(profileId: String): Boolean {
        val profile = activeProfiles[profileId] ?: return false
        val pm = appContext.packageManager

        val launchIntent = pm.getLaunchIntentForPackage(profile.packageName) ?: return false

        // Tentukan stub process class berdasarkan userId
        val stubClass = when (profile.userId % 5) {
            0 -> StubActivity.P0::class.java
            1 -> StubActivity.P1::class.java
            2 -> StubActivity.P2::class.java
            3 -> StubActivity.P3::class.java
            else -> StubActivity.P4::class.java
        }

        val proxyIntent = Intent(appContext, stubClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TARGET_INTENT", launchIntent)
            putExtra("EXTRA_USER_ID", profile.userId)
            putExtra("EXTRA_PACKAGE_NAME", profile.packageName)
            putExtra("EXTRA_PROFILE_ID", profileId)
        }

        activeProfiles[profileId] = profile.copy(
            isRunning = true,
            lastLaunchedAt = System.currentTimeMillis()
        )

        appContext.startActivity(proxyIntent)
        Log.i(TAG, "Launched $profileId into stub process ${stubClass.simpleName}")
        return true
    }

    /**
     * Menghapus profil klon beserta seluruh folder data privatnya
     */
    fun deleteClone(profileId: String): Boolean {
        val profile = activeProfiles.remove(profileId) ?: return false
        return VFileSystem.deleteUserDataDir(appContext, profile.userId, profile.packageName)
    }

    /**
     * Mengambil daftar aplikasi sosial media / messaging yang terpasang di HP host
     */
    fun getInstalledClonableApps(): List<ClonableAppInfo> {
        val pm = appContext.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val result = mutableListOf<ClonableAppInfo>()

        for (app in installedApps) {
            // Abaikan system apps kecuali aplikasi umum
            val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            if (isSystem && !isWhitelistedSystemApp(app.packageName)) continue
            if (app.packageName == appContext.packageName) continue

            val label = pm.getApplicationLabel(app).toString()
            val icon = pm.getApplicationIcon(app)

            result.add(
                ClonableAppInfo(
                    packageName = app.packageName,
                    appName = label,
                    isSocialMedia = isSocialApp(app.packageName)
                )
            )
        }

        return result.sortedWith(compareByDescending<ClonableAppInfo> { it.isSocialMedia }.thenBy { it.appName })
    }

    private fun isSocialApp(packageName: String): Boolean {
        val socialKeywords = listOf(
            "whatsapp", "telegram", "facebook", "katana", "instagram",
            "tiktok", "twitter", "line", "viber", "discord", "wechat"
        )
        return socialKeywords.any { packageName.contains(it, ignoreCase = true) }
    }

    private fun isWhitelistedSystemApp(packageName: String): Boolean {
        return packageName.contains("chrome") || packageName.contains("youtube")
    }
}

data class ClonableAppInfo(
    val packageName: String,
    val appName: String,
    val isSocialMedia: Boolean
)
