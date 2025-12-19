package io.github.wifi_password_manager.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable data object NetworkListScreen : Route, NavKey

    @Serializable data object SettingScreen : Route, NavKey

    @Serializable data object LicenseScreen : Route, NavKey
}
