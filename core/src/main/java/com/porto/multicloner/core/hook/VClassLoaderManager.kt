package com.porto.multicloner.core.hook

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import dalvik.system.PathClassLoader
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object VClassLoaderManager {

    private const val TAG = "VClassLoaderManager"
    private val classLoaderCache = ConcurrentHashMap<String, ClassLoader>()

    /**
     * Membuat atau mengambil ClassLoader yang mencakup base.apk dan seluruh Split APKs target
     */
    fun getOrCreateClassLoader(context: Context, packageName: String): ClassLoader {
        return classLoaderCache.getOrPut(packageName) {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)

            val apkPaths = mutableListOf<String>()
            apkPaths.add(appInfo.publicSourceDir)
            appInfo.splitPublicSourceDirs?.let { splits ->
                apkPaths.addAll(splits)
            }

            val combinedApkPath = apkPaths.joinToString(File.pathSeparator)
            val nativeLibDir = appInfo.nativeLibraryDir

            Log.i(TAG, "Creating PathClassLoader for $packageName with APKs: $combinedApkPath and nativeLib: $nativeLibDir")

            PathClassLoader(
                combinedApkPath,
                nativeLibDir,
                context.classLoader
            )
        }
    }
}
