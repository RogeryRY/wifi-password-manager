@file:Suppress("DEPRECATION")

package io.github.wifi_password_manager.data

import android.content.Context
import androidx.annotation.StringRes
import io.github.wifi_password_manager.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WifiNetwork(
    @Transient val networkId: Int = -1,
    val ssid: String,
    @SerialName("security") val securityType: Set<SecurityType>,
    val password: String,
    val hidden: Boolean = false,
    val autojoin: Boolean = true,
) {
    companion object

    fun getSecurity(context: Context): String =
        securityType.joinToString("/") { context.getString(it.displayNameRes) }

    @Serializable
    enum class SecurityType {
        OPEN,
        OWE,
        WPA2,
        WPA3,
        WEP;

        @get:StringRes
        val displayNameRes: Int
            get() =
                when (this) {
                    OPEN -> R.string.security_open
                    OWE -> R.string.security_owe
                    WPA2 -> R.string.security_wpa2
                    WPA3 -> R.string.security_wpa3
                    WEP -> R.string.security_wep
                }
    }
}
