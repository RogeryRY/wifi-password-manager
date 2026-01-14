package io.github.wifi_password_manager;

import io.github.wifi_password_manager.ipc.WifiNetworkParcel;

interface IWifiRootService {
    List<WifiNetworkParcel> getPrivilegedConfiguredNetworks();

    boolean addOrUpdateNetworkPrivileged(in WifiNetworkParcel config);

    boolean removeNetwork(int netId);

    void persistEphemeralNetworks();
}

