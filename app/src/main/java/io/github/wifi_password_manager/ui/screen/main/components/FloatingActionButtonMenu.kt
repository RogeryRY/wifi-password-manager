package io.github.wifi_password_manager.ui.screen.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.wifi_password_manager.R
import io.github.wifi_password_manager.ui.theme.WiFiPasswordManagerTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainFloatingActionButtonMenu(
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                checked = fabMenuExpanded,
                onCheckedChange = { fabMenuExpanded = it },
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Menu
                    }
                }

                Icon(
                    imageVector = imageVector,
                    contentDescription = stringResource(R.string.more_options_description),
                    modifier = Modifier.animateIcon(checkedProgress = { checkedProgress }),
                )
            }
        },
        modifier = modifier,
    ) {
        FloatingActionButtonMenuItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.FileDownload,
                    contentDescription = stringResource(R.string.import_action),
                )
            },
            text = { Text(text = stringResource(R.string.import_action)) },
            onClick = {
                onImportClick()
                fabMenuExpanded = false
            },
        )

        FloatingActionButtonMenuItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.FileUpload,
                    contentDescription = stringResource(R.string.export_action),
                )
            },
            text = { Text(text = stringResource(R.string.export_action)) },
            onClick = {
                onExportClick()
                fabMenuExpanded = false
            },
        )
    }
}

@Preview
@Composable
private fun MainFloatingActionButtonMenuPreview() {
    WiFiPasswordManagerTheme {
        Scaffold(
            floatingActionButton = {
                MainFloatingActionButtonMenu(onImportClick = {}, onExportClick = {})
            }
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}
