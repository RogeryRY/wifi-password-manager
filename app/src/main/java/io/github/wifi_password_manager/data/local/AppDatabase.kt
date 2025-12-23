package io.github.wifi_password_manager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.wifi_password_manager.data.local.dao.WifiNetworkDao
import io.github.wifi_password_manager.data.local.entity.WifiNetworkEntity
import io.github.wifi_password_manager.data.local.entity.WifiNetworkFtsEntity

@TypeConverters
@Database(entities = [WifiNetworkEntity::class, WifiNetworkFtsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wifiNetworkDao(): WifiNetworkDao

    companion object {
        const val DATABASE_NAME = "app.db"
    }
}
