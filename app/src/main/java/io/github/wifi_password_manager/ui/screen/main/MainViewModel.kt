package io.github.wifi_password_manager.ui.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.wifi_password_manager.data.WifiNetwork
import io.github.wifi_password_manager.services.WifiService
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(FlowPreview::class)
class MainViewModel(private val wifiService: WifiService) : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    data class State(
        val savedNetworks: List<WifiNetwork> = emptyList(),
        val showingSearch: Boolean = false,
        val searchText: String = "",
    )

    sealed interface Event {
        data object GetSavedNetworks : Event

        data object ToggleSearch : Event

        data class SearchTextChanged(val text: String) : Event
    }

    private val cachedNetworks = mutableListOf<WifiNetwork>()

    private val _state = MutableStateFlow(State())
    val state =
        _state
            .onStart { onEvent(Event.GetSavedNetworks) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = State(),
            )

    init {
        state
            .distinctUntilChangedBy { it.searchText }
            .debounce(200.milliseconds)
            .map { state ->
                cachedNetworks
                    .filter { it.ssid.contains(state.searchText.trim(), ignoreCase = true) }
                    .sortedBy { it.ssid.lowercase() }
            }
            .onEach { networks -> _state.update { it.copy(savedNetworks = networks) } }
            .launchIn(viewModelScope)

        state
            .distinctUntilChangedBy { it.showingSearch }
            .map { it.showingSearch }
            .onEach { showing -> if (!showing) _state.update { it.copy(searchText = "") } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: Event) {
        Log.d(TAG, "onEvent: $event")
        viewModelScope.launch {
            when (event) {
                is Event.GetSavedNetworks -> getSavedNetworks()
                is Event.ToggleSearch ->
                    _state.update { it.copy(showingSearch = !it.showingSearch) }
                is Event.SearchTextChanged -> _state.update { it.copy(searchText = event.text) }
            }
        }
    }

    private suspend fun getSavedNetworks() {
        val networks = withContext(Dispatchers.IO) { wifiService.getPrivilegedConfiguredNetworks() }
        cachedNetworks.clear()
        cachedNetworks.addAll(networks)
        _state.update { it.copy(savedNetworks = networks) }
    }
}
