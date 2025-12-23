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
import io.github.wifi_password_manager.utils.groupAndSortedBySsid
import io.github.wifi_password_manager.utils.hasShizukuPermission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

class WifiRepositoryImpl(
    private val context: Context,
    private val wifiNetworkDao: WifiNetworkDao,
    dispatcher: CoroutineDispatcher,
) : WifiRepository {
    companion object {
        private const val TAG = "WifiService"
        private const val SHELL_PACKAGE = "com.android.shell"
    }

    private val _systemNetworks = MutableStateFlow<List<WifiNetwork>>(emptyList())

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

    init {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            _systemNetworks.collect { networks ->
                if (networks.isNotEmpty()) {
                    wifiNetworkDao.upsertNetworks(networks.map { it.toEntity() })

                    val systemNetworkIds = networks.map { it.networkId }
                    wifiNetworkDao.deleteNetworks(systemNetworkIds)
                }
            }
        }
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

    override fun refresh() {
        val configs = getPrivilegedConfiguredNetworks()
        _systemNetworks.update {
            configs.map(WifiNetwork::fromWifiConfiguration).groupAndSortedBySsid()
        }
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
}
