package io.github.wifi_password_manager.ui.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.wifi_password_manager.data.WifiNetwork
import io.github.wifi_password_manager.navigation.LocalNavBackStack
import io.github.wifi_password_manager.navigation.SettingScreen
import io.github.wifi_password_manager.ui.screen.main.components.MainFloatingActionButtonMenu
import io.github.wifi_password_manager.ui.screen.main.components.NetworkList
import io.github.wifi_password_manager.ui.screen.main.components.SearchBar
import io.github.wifi_password_manager.ui.shared.LoadingDialog
import io.github.wifi_password_manager.ui.theme.WiFiPasswordManagerTheme
import io.github.wifi_password_manager.utils.MOCK

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(state: MainViewModel.State, onEvent: (MainViewModel.Event) -> Unit) {
    val navBackStack = LocalNavBackStack.current

    BackHandler(enabled = state.showingSearch) { onEvent(MainViewModel.Event.ToggleSearch) }

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = state.showingSearch,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
            ) { showingSearch ->
                if (showingSearch) {
                    SearchBar(state = state, onEvent = onEvent)
                } else {
                    TopAppBar(
                        title = { Text(text = "Saved WiFi Networks") },
                        actions = {
                            TooltipBox(
                                positionProvider =
                                    TooltipDefaults.rememberTooltipPositionProvider(),
                                tooltip = { PlainTooltip { Text(text = "Search") } },
                                state = rememberTooltipState(),
                            ) {
                                IconButton(
                                    onClick = { onEvent(MainViewModel.Event.ToggleSearch) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = "Search",
                                    )
                                }
                            }

                            TooltipBox(
                                positionProvider =
                                    TooltipDefaults.rememberTooltipPositionProvider(),
                                tooltip = { PlainTooltip { Text(text = "Settings") } },
                                state = rememberTooltipState(),
                            ) {
                                IconButton(onClick = { navBackStack.add(SettingScreen) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Settings",
                                    )
                                }
                            }
                        },
                    )
                }
            }
        },
        floatingActionButton = {
            if (!state.showingSearch) {
                MainFloatingActionButtonMenu(
                    onImportClick = { onEvent(MainViewModel.Event.ImportNetworks) },
                    onExportClick = { onEvent(MainViewModel.Event.ExportNetworks) },
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { innerPadding ->
        when {
            state.showingSearch -> {
                NetworkList(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding(),
                    networks = state.savedNetworks,
                )
            }
            state.isLoading -> {
                LoadingDialog()
            }
            else -> {
                PullToRefreshBox(
                    modifier = Modifier.padding(innerPadding),
                    isRefreshing = false,
                    onRefresh = { onEvent(MainViewModel.Event.GetSavedNetworks) },
                ) {
                    NetworkList(
                        modifier = Modifier.fillMaxSize().imePadding(),
                        networks = state.savedNetworks,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MainViewPreview() {
    WiFiPasswordManagerTheme {
        MainView(state = MainViewModel.State(savedNetworks = WifiNetwork.MOCK), onEvent = {})
    }
}

@PreviewLightDark
@Composable
private fun LoadingMainViewPreview() {
    WiFiPasswordManagerTheme {
        MainView(state = MainViewModel.State(isLoading = true), onEvent = {})
    }
}

@PreviewLightDark
@Composable
private fun SearchMainViewPreview() {
    WiFiPasswordManagerTheme {
        MainView(state = MainViewModel.State(showingSearch = true), onEvent = {})
    }
}
