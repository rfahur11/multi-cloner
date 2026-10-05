package com.porto.multicloner

import android.app.Application
import android.content.Context
import com.porto.multicloner.core.VirtualCore

class ClonerApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // Inisialisasi engine virtualisasi pada tahap paling awal aplikasi
        VirtualCore.get().doStartup(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
