package com.amazon.tv.leanbacklauncher.apps;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.List;

public class ConnectivityListener {
    private final ConnectivityManager mConnectivityManager;
    private ConnectivityStatus mConnectivityStatus = new ConnectivityStatus();
    private final Context mContext;
    private final IntentFilter mFilter;
    private boolean mIsRegistered;
    private final Listener mListener;
    private final LeanbackLauncherPhoneStateListener mPhoneStateListener;
    private final BroadcastReceiver mReceiver;
    private final TelephonyManager mTelephonyManager;
    private final WifiManager mWifiManager;

    public interface Listener {
        void onConnectivityChange();
    }

    public static class ConnectivityStatus {
        public String mMobileNetworkName;
        public int mMobileSignalStrength;
        public int mNetworkType;
        public int mWifiSignalStrength;
        public String mWifiSsid;
    }

    private static class LeanbackLauncherPhoneStateListener extends PhoneStateListener {
        private WeakReference<ConnectivityListener> mListener;

        public LeanbackLauncherPhoneStateListener(ConnectivityListener listener) {
            this.mListener = new WeakReference(listener);
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            ConnectivityListener listener = (ConnectivityListener) this.mListener.get();
            if (listener != null) {
                listener.mConnectivityStatus.mMobileSignalStrength = ConnectivityListener.getLevel(signalStrength);
            }
        }
    }

    @TargetApi(23)
    private static int getLevel(SignalStrength signalStrength) {
        LinkageError e;
        try {
            return signalStrength.getLevel();
        } catch (NoClassDefFoundError e2) {
            e = e2;
            Log.e("ConnectivityListener", "Exception fetching signal level", e);
            return 0;
        } catch (IncompatibleClassChangeError e3) {
            e = e3;
            Log.e("ConnectivityListener", "Exception fetching signal level", e);
            return 0;
        }
    }

    public ConnectivityListener(Context context, Listener listener) {
        this.mContext = context;
        this.mConnectivityManager = (ConnectivityManager) this.mContext.getApplicationContext().getSystemService("connectivity");
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        this.mPhoneStateListener = new LeanbackLauncherPhoneStateListener(this);
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mListener = listener;
        this.mFilter = new IntentFilter();
        this.mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mFilter.addAction("android.net.wifi.RSSI_CHANGED");
        this.mFilter.addAction("android.net.conn.INET_CONDITION_ACTION");
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                boolean z = false;
                String intentAction = intent.getAction();
                int connectionStatus = intent.getIntExtra("inetCondition", -551);
                NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
                if (!(info != null && info.isAvailable() && info.isConnected())) {
                    ConnectivityListener.writeConnectivity(context, false);
                }
                if (intentAction.equals("android.net.conn.INET_CONDITION_ACTION") || (intentAction.equals("android.net.conn.CONNECTIVITY_CHANGE") && !ConnectivityListener.readConnectivity(ConnectivityListener.this.mContext))) {
                    if (connectionStatus > 50) {
                        z = true;
                    }
                    ConnectivityListener.writeConnectivity(context, z);
                }
                ConnectivityListener.this.updateConnectivityStatus();
                ConnectivityListener.this.mListener.onConnectivityChange();
            }
        };
    }

    private static void writeConnectivity(Context context, boolean inetConnected) {
        context.getSharedPreferences("inet-prefs", 0).edit().putBoolean("inetCondition", inetConnected).apply();
    }

    public static boolean readConnectivity(Context context) {
        return context.getSharedPreferences("inet-prefs", 0).getBoolean("inetCondition", true);
    }

    public void start() {
        if (!this.mIsRegistered) {
            updateConnectivityStatus();
            this.mContext.registerReceiver(this.mReceiver, this.mFilter);
            this.mTelephonyManager.listen(this.mPhoneStateListener, 256);
            this.mIsRegistered = true;
        }
    }

    public void stop() {
        if (this.mIsRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
            this.mIsRegistered = false;
        }
    }

    public ConnectivityStatus getConnectivityStatus() {
        return this.mConnectivityStatus;
    }

    private void setNoConnection() {
        this.mConnectivityStatus.mNetworkType = 1;
        this.mConnectivityStatus.mWifiSsid = null;
        this.mConnectivityStatus.mWifiSignalStrength = 0;
    }

    private boolean isSecureWifi(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return false;
        }
        int networkId = wifiInfo.getNetworkId();
        List<WifiConfiguration> configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        if (configuredNetworks == null) {
            return false;
        }
        for (WifiConfiguration configuredNetwork : configuredNetworks) {
            if (configuredNetwork.networkId == networkId) {
                if (configuredNetwork.allowedKeyManagement.get(1) || configuredNetwork.allowedKeyManagement.get(2) || configuredNetwork.allowedKeyManagement.get(3)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private void updateConnectivityStatus() {
    	// deprecated in API29
        NetworkInfo networkInfo = this.mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            setNoConnection();
            return;
        }
        boolean isConnected = readConnectivity(this.mContext);
        switch (networkInfo.getType()) {
            case 0:
                if (isConnected) {
                    this.mConnectivityStatus.mNetworkType = 13;
                } else {
                    this.mConnectivityStatus.mNetworkType = 15;
                }
                String operator = null;
                if (this.mTelephonyManager != null) {
                    operator = this.mTelephonyManager.getNetworkOperatorName();
                    if (operator != null) {
                        operator = removeDoubleQuotes(operator);
                    }
                }
                if (operator != null) {
                    this.mConnectivityStatus.mMobileNetworkName = operator;
                } else {
                    this.mConnectivityStatus.mMobileNetworkName = "";
                }
                this.mConnectivityStatus.mWifiSsid = null;
                this.mConnectivityStatus.mWifiSignalStrength = 0;
                return;
            case 1:
                WifiInfo wifiInfo = this.mWifiManager.getConnectionInfo();
                if (isSecureWifi(wifiInfo)) {
                    this.mConnectivityStatus.mNetworkType = 5;
                } else {
                    this.mConnectivityStatus.mNetworkType = 3;
                }
                if (!isConnected) {
                    this.mConnectivityStatus.mNetworkType = 9;
                }
                String ssid = null;
                if (wifiInfo != null) {
                    ssid = wifiInfo.getSSID();
                    if (ssid != null) {
                        ssid = removeDoubleQuotes(ssid);
                    }
                }
                if (ssid != null) {
                    this.mConnectivityStatus.mWifiSsid = ssid.equals("<unknown ssid>") ? "" : ssid;
                } else {
                    this.mConnectivityStatus.mWifiSsid = "";
                }
                if (wifiInfo != null) {
                    this.mConnectivityStatus.mWifiSignalStrength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
                } else {
                    this.mConnectivityStatus.mWifiSignalStrength = 0;
                }
                this.mConnectivityStatus.mMobileNetworkName = null;
                this.mConnectivityStatus.mMobileSignalStrength = 0;
                return;
            case 9:
                if (isConnected) {
                    this.mConnectivityStatus.mNetworkType = 7;
                } else {
                    this.mConnectivityStatus.mNetworkType = 11;
                }
                this.mConnectivityStatus.mWifiSsid = null;
                this.mConnectivityStatus.mWifiSignalStrength = 0;
                this.mConnectivityStatus.mMobileNetworkName = null;
                this.mConnectivityStatus.mMobileSignalStrength = 0;
                return;
            default:
                setNoConnection();
                return;
        }
    }

    public static String removeDoubleQuotes(String string) {
        if (string == null) {
            return null;
        }
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }
}
