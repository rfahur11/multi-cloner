package com.porto.multicloner.core.spoof

import com.porto.multicloner.core.model.DeviceIdentity
import java.security.SecureRandom

object DeviceSpoofManager {

    private val random = SecureRandom()

    private val POPULAR_DEVICES = listOf(
        Pair("samsung", "SM-S928B"), // Galaxy S24 Ultra
        Pair("samsung", "SM-A556B"), // Galaxy A55
        Pair("Xiaomi", "23127PN0CG"), // Xiaomi 14
        Pair("Xiaomi", "2311DRK48G"), // POCO X6 Pro
        Pair("OPPO", "CPH2581"),      // OnePlus 12
        Pair("vivo", "V2324A"),       // vivo X100
        Pair("Google", "Pixel 8 Pro")
    )

    /**
     * Membuat KTP / Sidik jari perangkat virtual yang acak dan realistis
     */
    fun generateRandomIdentity(): DeviceIdentity {
        val device = POPULAR_DEVICES[random.nextInt(POPULAR_DEVICES.size)]
        return DeviceIdentity(
            androidId = generateHex(16),
            imei = generateImei(),
            buildModel = device.second,
            manufacturer = device.first,
            macAddress = generateMacAddress()
        )
    }

    private fun generateHex(length: Int): String {
        val chars = "0123456789abcdef"
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(chars[random.nextInt(chars.length)])
        }
        return sb.toString()
    }

    private fun generateImei(): String {
        // Standard 15 digits IMEI generator
        val sb = StringBuilder("86")
        for (i in 0 until 12) {
            sb.append(random.nextInt(10))
        }
        // Luhn algorithm check digit
        sb.append(random.nextInt(10))
        return sb.toString()
    }

    private fun generateMacAddress(): String {
        val bytes = ByteArray(6)
        random.nextBytes(bytes)
        bytes[0] = (bytes[0].toInt() and 0xFE.toByte().toInt() or 0x02).toByte() // unicast & locally administered
        return bytes.joinToString(":") { "%02X".format(it) }
    }
}
