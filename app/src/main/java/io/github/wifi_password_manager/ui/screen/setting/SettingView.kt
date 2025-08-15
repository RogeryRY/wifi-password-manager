package io.github.wifi_password_manager.ui.screen.setting

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import io.github.wifi_password_manager.BuildConfig
import io.github.wifi_password_manager.R
import io.github.wifi_password_manager.navigation.LocalNavBackStack
import io.github.wifi_password_manager.ui.screen.setting.components.ForgetAllNetworksItem
import io.github.wifi_password_manager.ui.screen.setting.components.SettingSection
import io.github.wifi_password_manager.ui.screen.setting.components.ThemeModeItem
import io.github.wifi_password_manager.ui.shared.LoadingDialog
import io.github.wifi_password_manager.ui.theme.WiFiPasswordManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingView(state: SettingViewModel.State, onEvent: (SettingViewModel.Event) -> Unit) {
    val navBackStack = LocalNavBackStack.current

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navBackStack.removeLastOrNull() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = { Text(text = stringResource(R.string.settings_title)) },
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Appearance Section
            item {
                SettingSection(title = stringResource(R.string.appearance_section)) {
                    ThemeModeItem(themeMode = state.settings.themeMode) {
                        onEvent(SettingViewModel.Event.UpdateThemeMode(it))
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainer)

                        ListItem(
                            modifier =
                                Modifier.clickable {
                                    onEvent(
                                        SettingViewModel.Event.ToggleMaterialYou(
                                            !state.settings.useMaterialYou
                                        )
                                    )
                                },
                            headlineContent = {
                                Text(text = stringResource(R.string.material_you_title))
                            },
                            trailingContent = {
                                Switch(
                                    checked = state.settings.useMaterialYou,
                                    onCheckedChange = {
                                        onEvent(SettingViewModel.Event.ToggleMaterialYou(it))
                                    },
                                )
                            },
                        )
                    }
                }
            }

            // Import & Export Section
            item {
                SettingSection(title = stringResource(R.string.import_export_section)) {
                    ListItem(
                        modifier =
                            Modifier.clickable { onEvent(SettingViewModel.Event.ImportNetworks) },
                        headlineContent = { Text(text = stringResource(R.string.import_action)) },
                        supportingContent = {
                            Text(text = stringResource(R.string.import_description))
                        },
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainer)

                    ListItem(
                        modifier =
                            Modifier.clickable { onEvent(SettingViewModel.Event.ExportNetworks) },
                        headlineContent = { Text(text = stringResource(R.string.export_action)) },
                        supportingContent = {
                            Text(text = stringResource(R.string.export_description))
                        },
                    )
                }
            }

            // Advanced Section
            item {
                SettingSection(title = stringResource(R.string.advanced_section)) {
                    ForgetAllNetworksItem { onEvent(SettingViewModel.Event.ForgetAllNetworks) }
                }
            }

            // About Section
            item {
                SettingSection(title = stringResource(R.string.about_section)) {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.version_title)) },
                        supportingContent = { Text(text = BuildConfig.VERSION_NAME) },
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainer)

                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(R.string.build_type_title))
                        },
                        supportingContent = { Text(text = BuildConfig.BUILD_TYPE) },
                    )
                }
            }
        }

        if (state.isLoading) {
            LoadingDialog()
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingViewPreview() {
    WiFiPasswordManagerTheme { SettingView(state = SettingViewModel.State(), onEvent = {}) }
}

@PreviewScreenSizes
@Composable
private fun AdaptiveSettingViewPreview() {
    WiFiPasswordManagerTheme { SettingView(state = SettingViewModel.State(), onEvent = {}) }
}
