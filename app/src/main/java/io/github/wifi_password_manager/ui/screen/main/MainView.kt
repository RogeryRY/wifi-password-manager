package io.github.wifi_password_manager.ui.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.wifi_password_manager.R
import io.github.wifi_password_manager.data.WifiNetwork
import io.github.wifi_password_manager.navigation.LocalNavBackStack
import io.github.wifi_password_manager.navigation.SettingScreen
import io.github.wifi_password_manager.ui.screen.main.components.NetworkList
import io.github.wifi_password_manager.ui.screen.main.components.SearchBar
import io.github.wifi_password_manager.ui.theme.WiFiPasswordManagerTheme
import io.github.wifi_password_manager.utils.MOCK
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(state: MainViewModel.State, onEvent: (MainViewModel.Event) -> Unit) {
    val navBackStack = LocalNavBackStack.current

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    val showScrollToTop by remember {
        derivedStateOf {
            val listScrolled =
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
            val gridScrolled =
                gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0

            listScrolled || gridScrolled
        }
    }

    BackHandler(enabled = state.showingSearch) { onEvent(MainViewModel.Event.ToggleSearch) }

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            AnimatedContent(
                targetState = state.showingSearch,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
            ) { showingSearch ->
                if (showingSearch) {
                    SearchBar(state = state, onEvent = onEvent)
                } else {
                    TopAppBar(
                        title = { Text(text = stringResource(R.string.main_title)) },
                        actions = {
                            TooltipBox(
                                positionProvider =
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        positioning = TooltipAnchorPosition.Below
                                    ),
                                tooltip = {
                                    PlainTooltip {
                                        Text(text = stringResource(R.string.search_tooltip))
                                    }
                                },
                                state = rememberTooltipState(),
                            ) {
                                IconButton(
                                    onClick = { onEvent(MainViewModel.Event.ToggleSearch) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = stringResource(R.string.search_tooltip),
                                    )
                                }
                            }

                            TooltipBox(
                                positionProvider =
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        positioning = TooltipAnchorPosition.Below
                                    ),
                                tooltip = {
                                    PlainTooltip {
                                        Text(text = stringResource(R.string.settings_tooltip))
                                    }
                                },
                                state = rememberTooltipState(),
                            ) {
                                IconButton(onClick = { navBackStack.add(SettingScreen) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription =
                                            stringResource(R.string.settings_tooltip),
                                    )
                                }
                            }
                        },
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(visible = showScrollToTop, enter = fadeIn(), exit = fadeOut()) {
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(
                            positioning = TooltipAnchorPosition.Above
                        ),
                    tooltip = {
                        PlainTooltip {
                            Text(text = stringResource(R.string.scroll_to_top_description))
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    FloatingActionButton(
                        modifier = Modifier.navigationBarsPadding().imePadding(),
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                                gridState.animateScrollToItem(0)
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = stringResource(R.string.scroll_to_top_description),
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { innerPadding ->
        val modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding()

        when {
            state.showingSearch -> {
                NetworkList(
                    modifier = modifier,
                    networks = state.savedNetworks,
                    listState = listState,
                    gridState = gridState,
                )
            }
            else -> {
                PullToRefreshBox(
                    modifier = modifier,
                    isRefreshing = false,
                    onRefresh = { onEvent(MainViewModel.Event.GetSavedNetworks) },
                ) {
                    NetworkList(
                        modifier = Modifier.fillMaxSize(),
                        networks = state.savedNetworks,
                        listState = listState,
                        gridState = gridState,
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
private fun SearchMainViewPreview() {
    WiFiPasswordManagerTheme {
        MainView(state = MainViewModel.State(showingSearch = true), onEvent = {})
    }
}
