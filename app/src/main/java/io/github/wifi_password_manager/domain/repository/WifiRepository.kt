@file:Suppress("DEPRECATION")

package io.github.wifi_password_manager.domain.repository

import android.net.wifi.WifiConfiguration
import io.github.wifi_password_manager.domain.model.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface WifiRepository {
    fun getAllNetworks(): Flow<List<WifiNetwork>>

    fun getAllNetworks(query: String): Flow<List<WifiNetwork>>

    suspend fun getAllNetworksList(): List<WifiNetwork>

    suspend fun getNetworkCount(): Int

    suspend fun refresh()

    fun getPrivilegedConfiguredNetworks(): List<WifiConfiguration>

    fun addOrUpdateNetworkPrivileged(config: WifiConfiguration): Boolean

    fun removeNetwork(netId: Int): Boolean

    suspend fun updateNote(ssid: String, note: String?)
}
