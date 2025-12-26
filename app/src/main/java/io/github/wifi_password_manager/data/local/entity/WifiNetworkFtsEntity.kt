package io.github.wifi_password_manager.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = WifiNetworkEntity::class)
@Entity(tableName = "wifi_networks_fts")
data class WifiNetworkFtsEntity(val ssid: String, val note: String)
