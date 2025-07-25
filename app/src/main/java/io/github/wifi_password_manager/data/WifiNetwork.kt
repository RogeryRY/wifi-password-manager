@file:Suppress("DEPRECATION")

package io.github.wifi_password_manager.data

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

    val security: String
        get() = securityType.joinToString("/") { it.displayName }

    @Serializable
    enum class SecurityType {
        OPEN,
        OWE,
        WPA2,
        WPA3,
        WEP;

        val displayName: String
            get() =
                when (this) {
                    OPEN -> "Open"
                    OWE -> "OWE"
                    WPA2 -> "WPA/WPA2"
                    WPA3 -> "WPA3"
                    WEP -> "WEP"
                }
    }
}
