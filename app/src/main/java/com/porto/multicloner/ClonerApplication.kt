package com.porto.multicloner

import android.app.Application
import android.content.Context
import com.porto.multicloner.core.VirtualCore

class ClonerApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi engine virtualisasi saat Application context sudah terikat sempurna
        VirtualCore.get().doStartup(this)
    }
}
