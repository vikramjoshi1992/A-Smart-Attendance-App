package com.example.pranjalkanojiya.yessir;

/**
 * Created by Pranjal Kanojiya on 9/24/2016.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class WarDrive extends Service {

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Toast.makeText(this,"It has Begun.",Toast.LENGTH_LONG).show();
        int count=1;
        File data = null;
        File wifiRSSI = null;
        FileWriter wifiRSSIW = null;
        WifiManager wm = null;
        ConnectivityManager cm = null ;
        List<ScanResult> wifilist = null;
        WifiInfo wi =null;
        TelephonyManager tm = null;
        //EditText filename= null;
        try {
            data = new File(Environment.getExternalStorageDirectory()+File.separator+"yessirdata");
            //Toast.makeText(this,Environment.getExternalStorageDirectory().toString(),Toast.LENGTH_LONG).show();
            if(!data.exists())
                if(data.mkdir())
                    Toast.makeText(this,"Folder Created!",Toast.LENGTH_LONG).show();
            wifiRSSI = new File(data,"wifirssi.txt");
            wifiRSSIW = new FileWriter(wifiRSSI);
            wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            wi = wm.getConnectionInfo();
            wifilist = wm.getScanResults();
            tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            for(int i = 0 ; i < wifilist.size() ; i++)
            {
                wifiRSSIW.write(wifilist.get(i).toString()+"\n");
                wifiRSSIW.write(android.text.format.Formatter.formatIpAddress(wi.getIpAddress()).toString()+"\n");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                wifiRSSIW.flush();
                wifiRSSIW.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this,"It has come to an end.",Toast.LENGTH_LONG).show();
    }
}
