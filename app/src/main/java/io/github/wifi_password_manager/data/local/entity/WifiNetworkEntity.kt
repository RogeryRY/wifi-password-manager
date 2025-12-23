package io.github.wifi_password_manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.wifi_password_manager.domain.model.WifiNetwork
import kotlinx.collections.immutable.toImmutableSet

@Entity(tableName = "wifi_networks")
data class WifiNetworkEntity(
    @PrimaryKey val networkId: Int,
    val ssid: String,
    val securityTypes: String,
    val password: String,
    val hidden: Boolean,
    val autojoin: Boolean,
    val private: Boolean,
)

fun WifiNetwork.toEntity(): WifiNetworkEntity {
    return WifiNetworkEntity(
        networkId = networkId,
        ssid = ssid,
        securityTypes = securityType.joinToString(",") { it.name },
        password = password,
        hidden = hidden,
        autojoin = autojoin,
        private = private,
    )
}

fun WifiNetworkEntity.toDomain(): WifiNetwork {
    return WifiNetwork(
        networkId = networkId,
        ssid = ssid,
        securityType =
            securityTypes.split(",").map(WifiNetwork.SecurityType::valueOf).toImmutableSet(),
        password = password,
        hidden = hidden,
        autojoin = autojoin,
        private = private,
    )
}
