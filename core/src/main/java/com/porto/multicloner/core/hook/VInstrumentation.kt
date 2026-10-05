package com.porto.multicloner.core.hook

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class VInstrumentation(
    val base: Instrumentation,
    private val hostContext: Context
) : Instrumentation() {

    companion object {
        private const val TAG = "VInstrumentation"

        fun install(context: Context): Boolean {
            return try {
                VHiddenApiBypass.bypass()

                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
                currentActivityThreadMethod.isAccessible = true
                val activityThread = currentActivityThreadMethod.invoke(null) ?: return false

                val mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation")
                mInstrumentationField.isAccessible = true
                val originalInstrumentation = mInstrumentationField.get(activityThread) as Instrumentation

                if (originalInstrumentation is VInstrumentation) {
                    return true // Already installed
                }

                val vInstrumentation = VInstrumentation(originalInstrumentation, context.applicationContext)
                mInstrumentationField.set(activityThread, vInstrumentation)
                Log.i(TAG, "VInstrumentation installed successfully into ActivityThread")
                true
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to install VInstrumentation: ${t.message}", t)
                false
            }
        }
    }

    override fun newActivity(cl: ClassLoader, className: String, intent: Intent): Activity {
        val targetPackage = intent.getStringExtra("EXTRA_PACKAGE_NAME")
        val targetActivity = intent.getStringExtra("EXTRA_TARGET_ACTIVITY")

        if (targetPackage != null && targetActivity != null) {
            try {
                Log.i(TAG, "Intercepting Activity launch: Swapping $className -> $targetActivity (Target: $targetPackage)")
                val targetClassLoader = VClassLoaderManager.getOrCreateClassLoader(hostContext, targetPackage)
                return super.newActivity(targetClassLoader, targetActivity, intent)
            } catch (t: Throwable) {
                Log.e(TAG, "Error swapping Activity to $targetActivity: ${t.message}. Falling back to default.", t)
            }
        }

        return super.newActivity(cl, className, intent)
    }

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle?) {
        val intent = activity.intent
        val targetPackage = intent?.getStringExtra("EXTRA_PACKAGE_NAME")
        val userId = intent?.getIntExtra("EXTRA_USER_ID", 0) ?: 0

        if (targetPackage != null) {
            Log.i(TAG, "Preparing context injection for ${activity.javaClass.name} ($targetPackage, user: $userId)")
            VContextInjector.inject(activity, targetPackage, userId)
        }

        super.callActivityOnCreate(activity, icicle)
    }
}
