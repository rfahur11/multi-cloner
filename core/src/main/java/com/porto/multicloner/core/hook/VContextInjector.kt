package com.porto.multicloner.core.hook

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import com.porto.multicloner.core.storage.VFileSystem
import java.io.File
import java.lang.reflect.Field

object VContextInjector {

    private const val TAG = "VContextInjector"

    /**
     * Menyuntikkan Resources, Theme, dan Path direktori data terisolasi ke dalam Activity target
     */
    fun inject(activity: Activity, targetPackage: String, userId: Int) {
        try {
            val targetContext = activity.createPackageContext(
                targetPackage,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )

            val targetResources = targetContext.resources
            val targetTheme = targetContext.theme

            // 1. Inject Resources field di ContextThemeWrapper
            replaceField(activity, "mResources", targetResources)

            // 2. Inject Theme field
            replaceField(activity, "mTheme", targetTheme)

            // 3. Siapkan folder terisolasi untuk user space ini
            val isolatedDir = VFileSystem.getUserDataDir(activity, userId, targetPackage)
            Log.i(TAG, "Injected target resources and isolated storage: ${isolatedDir.absolutePath}")

        } catch (t: Throwable) {
            Log.e(TAG, "Failed to inject resources for $targetPackage: ${t.message}", t)
        }
    }

    private fun replaceField(target: Any, fieldName: String, value: Any?) {
        var clazz: Class<*>? = target.javaClass
        while (clazz != null && clazz != Any::class.java) {
            try {
                val field: Field = clazz.getDeclaredField(fieldName)
                field.isAccessible = true
                field.set(target, value)
                return
            } catch (_: NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }
    }
}
