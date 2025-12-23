package io.github.wifi_password_manager.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.wifi_password_manager.data.local.entity.WifiNetworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiNetworkDao {
    @Query("SELECT * FROM wifi_networks ORDER BY ssid ASC")
    fun getAllNetworks(): Flow<List<WifiNetworkEntity>>

    @Query(
        """
            SELECT wifi_networks.* FROM wifi_networks
            JOIN wifi_networks_fts ON wifi_networks.rowid = wifi_networks_fts.rowid
            WHERE wifi_networks_fts MATCH :query
        """
    )
    fun getAllNetworks(query: String): Flow<List<WifiNetworkEntity>>

    @Query("SELECT * FROM wifi_networks ORDER BY ssid ASC")
    suspend fun getAllNetworksList(): List<WifiNetworkEntity>

    @Upsert suspend fun upsertNetworks(networks: List<WifiNetworkEntity>)

    @Upsert suspend fun upsertNetwork(network: WifiNetworkEntity)

    @Query("DELETE FROM wifi_networks WHERE networkId = :networkId")
    suspend fun deleteNetwork(networkId: Int)

    @Query("DELETE FROM wifi_networks WHERE networkId NOT IN (:excludingNetworkIds)")
    suspend fun deleteNetworks(excludingNetworkIds: List<Int>)

    @Query("DELETE FROM wifi_networks") suspend fun deleteNetworks()

    @Query("SELECT COUNT(*) FROM wifi_networks") suspend fun getNetworkCount(): Int
}
