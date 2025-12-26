@file:Suppress("DEPRECATION")

package io.github.wifi_password_manager.data.repository

import android.content.AttributionSource
import android.content.AttributionSourceHidden
import android.content.Context
import android.net.wifi.IWifiManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import dev.rikka.tools.refine.Refine
import io.github.wifi_password_manager.data.local.dao.WifiNetworkDao
import io.github.wifi_password_manager.data.local.entity.toDomain
import io.github.wifi_password_manager.data.local.entity.toEntity
import io.github.wifi_password_manager.domain.model.WifiNetwork
import io.github.wifi_password_manager.domain.repository.WifiRepository
import io.github.wifi_password_manager.utils.fromWifiConfiguration
import io.github.wifi_password_manager.utils.hasShizukuPermission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.invoke
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

class WifiRepositoryImpl(
    private val context: Context,
    private val wifiNetworkDao: WifiNetworkDao,
    private val dispatcher: CoroutineDispatcher,
) : WifiRepository {
    companion object {
        private const val TAG = "WifiService"
        private const val SHELL_PACKAGE = "com.android.shell"
    }

    private val wifiManager by lazy {
        SystemServiceHelper.getSystemService(Context.WIFI_SERVICE)
            .let(::ShizukuBinderWrapper)
            .let(IWifiManager.Stub::asInterface)
    }

    private val user
        get() =
            when (Shizuku.getUid()) {
                0 -> "root"
                1000 -> "system"
                2000 -> "shell"
                else -> throw IllegalArgumentException("Unknown Shizuku user ${Shizuku.getUid()}")
            }

    private val attributionSource: AttributionSource? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Refine.unsafeCast(
                AttributionSourceHidden(Shizuku.getUid(), SHELL_PACKAGE, SHELL_PACKAGE, null, null)
            )
        } else {
            null
        }
    }

    private suspend fun syncNetworksToDatabase(networks: List<WifiNetwork>) = dispatcher {
        if (networks.isEmpty()) {
            wifiNetworkDao.deleteNetworks()
            return@dispatcher
        }

        val existingNetworks = wifiNetworkDao.getAllNetworksList()
        val networksWithNotes =
            networks.map { network ->
                val existingNote = existingNetworks.firstOrNull { it.ssid == network.ssid }?.note
                network.copy(note = existingNote ?: network.note).toEntity()
            }
        wifiNetworkDao.upsertNetworks(networksWithNotes)

        val systemSsids = networks.map { it.ssid }
        wifiNetworkDao.deleteNetworks(systemSsids)
    }

    override fun getAllNetworks(): Flow<List<WifiNetwork>> =
        wifiNetworkDao.getAllNetworks().map { entities -> entities.map { it.toDomain() } }

    override fun getAllNetworks(query: String): Flow<List<WifiNetwork>> =
        wifiNetworkDao.getAllNetworks(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAllNetworksList(): List<WifiNetwork> {
        return wifiNetworkDao.getAllNetworksList().map { it.toDomain() }
    }

    override suspend fun getNetworkCount(): Int {
        return wifiNetworkDao.getNetworkCount()
    }

    override suspend fun refresh() {
        val configs = getPrivilegedConfiguredNetworks()
        val networks = configs.map(WifiNetwork::fromWifiConfiguration)
        syncNetworksToDatabase(networks)
    }

    override fun getPrivilegedConfiguredNetworks(): List<WifiConfiguration> {
        if (!context.hasShizukuPermission) {
            Log.w(TAG, "Shizuku permission not available, returning empty list")
            return emptyList()
        }
        val configs =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val bundle =
                    Bundle().apply {
                        putParcelable("EXTRA_PARAM_KEY_ATTRIBUTION_SOURCE", attributionSource)
                    }
                wifiManager.getPrivilegedConfiguredNetworks(user, SHELL_PACKAGE, bundle)?.list
            } else {
                wifiManager.getPrivilegedConfiguredNetworks(user, SHELL_PACKAGE)?.list
            }
        return configs.orEmpty()
    }

    override fun addOrUpdateNetworkPrivileged(config: WifiConfiguration): Boolean {
        if (!context.hasShizukuPermission) {
            Log.w(TAG, "Shizuku permission not available, cannot add/update network")
            return false
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val result = wifiManager.addOrUpdateNetworkPrivileged(config, user)
            result?.statusCode == WifiManager.AddNetworkResult.STATUS_SUCCESS
        } else {
            wifiManager.addOrUpdateNetwork(config, user) != -1
        }
    }

    override fun removeNetwork(netId: Int): Boolean {
        if (!context.hasShizukuPermission) {
            Log.w(TAG, "Shizuku permission not available, cannot remove network")
            return false
        }
        return wifiManager.removeNetwork(netId, user)
    }

    override suspend fun updateNote(ssid: String, note: String?) {
        wifiNetworkDao.updateNote(ssid, note)
    }
}
