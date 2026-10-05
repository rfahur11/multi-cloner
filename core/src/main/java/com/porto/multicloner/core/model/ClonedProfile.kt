package com.porto.multicloner.core.model

import java.io.Serializable

data class ClonedProfile(
    val id: String,
    val packageName: String,
    val appName: String,
    val userId: Int,
    val aliasName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLaunchedAt: Long = 0L,
    val isRunning: Boolean = false,
    val deviceIdentity: DeviceIdentity
) : Serializable

data class DeviceIdentity(
    val androidId: String,
    val imei: String,
    val buildModel: String,
    val manufacturer: String,
    val macAddress: String
) : Serializable
