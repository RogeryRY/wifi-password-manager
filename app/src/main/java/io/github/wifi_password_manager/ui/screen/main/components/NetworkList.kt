package io.github.wifi_password_manager.ui.screen.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.wifi_password_manager.data.WifiNetwork
import io.github.wifi_password_manager.ui.theme.WiFiPasswordManagerTheme
import io.github.wifi_password_manager.utils.MOCK

@Composable
fun NetworkList(modifier: Modifier = Modifier, networks: List<WifiNetwork>) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = networks, key = { it.ssid }) { network -> WifiCard(network = network) }
    }
}

@PreviewLightDark
@Composable
private fun NetworkListPreview() {
    WiFiPasswordManagerTheme { NetworkList(networks = WifiNetwork.MOCK) }
}
