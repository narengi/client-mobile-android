package xyz.narengi.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import xyz.narengi.android.ui.widget.TwoDScrollView;

/**
 * @author Siavash Mahmoudpour
 */
public class NetworkUtil {

    private static NetworkUtil instance;

    private NetworkUtil() {
    }

    public static NetworkUtil getInstance() {
        if (instance == null)
            instance = new NetworkUtil();

        return instance;
    }


    public boolean isNetworkConnected(Context context) {

        boolean isDataPresent = false;

        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        /*final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimSerialNumber() != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY && dataNetworkInfo != null) {
            isDataPresent = true;
        }*/

        return (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) || (dataNetworkInfo != null && dataNetworkInfo.isConnected());

    }
}
