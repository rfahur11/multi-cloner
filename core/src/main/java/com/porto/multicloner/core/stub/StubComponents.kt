package com.porto.multicloner.core.stub

import android.app.Activity
import android.app.Service
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.IBinder

// --- STUB ACTIVITIES ---
sealed class StubActivity : Activity() {
    class P0 : StubActivity()
    class P1 : StubActivity()
    class P2 : StubActivity()
    class P3 : StubActivity()
    class P4 : StubActivity()
}

// --- STUB SERVICES ---
sealed class StubService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    class P0 : StubService()
    class P1 : StubService()
    class P2 : StubService()
    class P3 : StubService()
    class P4 : StubService()
}

// --- STUB CONTENT PROVIDERS ---
sealed class StubContentProvider : ContentProvider() {
    override fun onCreate(): Boolean = true
    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    class P0 : StubContentProvider()
    class P1 : StubContentProvider()
    class P2 : StubContentProvider()
    class P3 : StubContentProvider()
    class P4 : StubContentProvider()
}
