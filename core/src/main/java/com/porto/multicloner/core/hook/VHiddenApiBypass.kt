package com.porto.multicloner.core.hook

import android.os.Build
import android.util.Log
import java.lang.reflect.Method

object VHiddenApiBypass {

    private const val TAG = "VHiddenApiBypass"

    /**
     * Mem-bypass pembatasan Hidden API Android (L-greylist & blacklist)
     * Menggunakan teknik double-reflection (safe for Android 9 - 15)
     */
    fun bypass(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return true
        }

        return try {
            val forNameMethod = Class::class.java.getDeclaredMethod("forName", String::class.java)
            val getDeclaredMethod = Class::class.java.getDeclaredMethod(
                "getDeclaredMethod",
                String::class.java,
                arrayOf<Class<*>>()::class.java
            )

            val vmRuntimeClass = forNameMethod.invoke(null, "dalvik.system.VMRuntime") as Class<*>
            val getRuntimeMethod = getDeclaredMethod.invoke(
                vmRuntimeClass,
                "getRuntime",
                null
            ) as Method
            val setHiddenApiExemptionsMethod = getDeclaredMethod.invoke(
                vmRuntimeClass,
                "setHiddenApiExemptions",
                arrayOf(arrayOf<String>()::class.java)
            ) as Method

            val vmRuntime = getRuntimeMethod.invoke(null)
            // Exempt all L-packages ("L" matches all internal framework APIs)
            setHiddenApiExemptionsMethod.invoke(vmRuntime, arrayOf("L"))
            Log.i(TAG, "Hidden API restrictions bypassed successfully")
            true
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to bypass hidden API: ${t.message}")
            false
        }
    }
}
