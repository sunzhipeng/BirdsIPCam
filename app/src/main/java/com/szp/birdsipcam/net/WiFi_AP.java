package com.szp.birdsipcam.net;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 至鹏 on 2018/6/28.
 */

public class WiFi_AP {

    private static final String TAG = "WiFi_AP";
    private static final String WIFI_HOTSPOT_SSID = "Birds_AP";  //  热点名称

    public WiFi_AP(WifiManager wifi) {
        createWifiHotspot(wifi);
    }

    /**
     * 创建Wifi热点
     */
    private void createWifiHotspot(WifiManager wifi) {

        if (wifi.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            wifi.setWifiEnabled(false);
        }

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = WIFI_HOTSPOT_SSID;
        config.preSharedKey = "123456789";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN); // 开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;

        // 通过反射调用设置热点
        try {
            Method method = wifi.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(wifi, config, true);
            if (enable) {
                Log.i(TAG, "createWifiHotspot: " + "热点已开启 SSID:" + WIFI_HOTSPOT_SSID + " password:123456789");
            } else {
                Log.i(TAG, "createWifiHotspot: 创建热点失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "createWifiHotspot: 创建热点失败");
        }
    }


    /**
     * 关闭WiFi热点
     */
    public void closeWifiHotspot(WifiManager wifi) {
        try {
            Method method = wifi.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(wifi);
            Method method2 = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(wifi, config, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "closeWifiHotspot: 热点已关闭");
        Log.i(TAG, "closeWifiHotspot: wifi已关闭");
    }
}
