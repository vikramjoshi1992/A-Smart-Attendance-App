package com.example.pranjalkanojiya.yessir;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pranjal Kanojiya on 9/29/2016.
 */
public class UserCollectWiFiData extends Service {

    class RoomData {
        String MAC;
        int lb;
        int ub;
        RoomData(String MAC,int lb,int ub)
        {
            this.MAC = MAC;
            this.lb = lb;
            this.ub = ub;
        }
    }
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId) {
        //Toast.makeText(this, "Starting Now.", Toast.LENGTH_LONG).show();
        File data = null;
        File wifiRSSI = null;
        FileWriter wifiRSSIW = null;
        WifiManager wm = null;
        ConnectivityManager cm = null ;
        List<ScanResult> wifilist = null;
        HashMap<String,Integer> macFamily = null;
        boolean present = true;
        ArrayList<RoomData> inroom119 = new ArrayList<RoomData>();
        ArrayList<RoomData> notinroom119 = new ArrayList<RoomData>();
        RoomData rb1 = new RoomData("00:26:5a:41:4e",60,100);
        RoomData rb2 = new RoomData("48:ee:0c:ee:27",70,100);
        RoomData rb3 = new RoomData("dc:a5:f4:b8:73",90,100);
        RoomData rb4 = new RoomData("e8:ba:70:9c:c7",55,100);
        RoomData rb5 = new RoomData("e8:ba:70:c2:44",70,100);
        RoomData rb6 = new RoomData("e8:ba:70:61:07",65,100);
        /*RoomData rb1 = new RoomData("e8:ba:70:52:79",30,100);
        RoomData rb2 = new RoomData("58:35:d9:aa:91",30,100);
        RoomData rb3 = new RoomData("6c:99:89:09:d7",30,100);
        RoomData rb4 = new RoomData("e8:ba:70:61:21",30,100);
        RoomData rb5 = new RoomData("e8:ba:70:92:cf",30,100);
        RoomData rb6 = new RoomData("e8:ba:70:9c:21",30,100);*/
        inroom119.add(rb1);
        inroom119.add(rb2);
        inroom119.add(rb3);
        inroom119.add(rb4);
        inroom119.add(rb5);
        inroom119.add(rb6);
        //inroom119.add(rb6);
        RoomData rb7 = new RoomData("14:dd:a9:70:e7",0,0);
        notinroom119.add(rb7);
        final String TAG = "UserCollectWiFiData";
        try {
            data = new File(Environment.getExternalStorageDirectory()+File.separator+"yessirdata");
            //Toast.makeText(this,"I am in.",Toast.LENGTH_LONG).show();
            if(!data.exists())
                if(data.mkdir())
                    Toast.makeText(this,"Folder Created!",Toast.LENGTH_LONG).show();
            wifiRSSI = new File(data,"sendToDBForAttE.txt");
            wifiRSSIW = new FileWriter(wifiRSSI);
            wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);

            wifilist = wm.getScanResults();
            macFamily = new HashMap<String, Integer>();
            for (int i = 0; i < wifilist.size();i++)
            {
                String capabli = wifilist.get(i).capabilities;
                Log.d("Mac Start",wifilist.get(i).BSSID.toString()+"::"+wifilist.get(i).SSID.toString()+"mac start");
                if((!(capabli.contains("[WPS]") || capabli.contains("[ESS]"))) || capabli.contains("WPA"))
                    continue;
                String temp1 = wifilist.get(i).BSSID.substring(0,14);
                Integer temp2 = new Integer(wifilist.get(i).level);
                temp2*=-1;
                Log.d(TAG,"("+temp1+")("+temp2.toString()+")");
                if(!macFamily.containsKey(temp1))
                {
                    Log.d("In if1 ","("+temp1+")("+temp2.toString()+")");
                    macFamily.put(temp1, temp2);
                }
                else
                {
                    if(temp2 < macFamily.get(temp1))
                        Log.d("In if2 ",macFamily.get(temp1).toString());
                        macFamily.put(temp1,temp2);
                }

            }
            Log.d("All unique",macFamily.toString());
            int count = 0;
            LOOPO : for(Map.Entry<String,Integer> e:macFamily.entrySet())
            {
                String tempmac = e.getKey();
                Integer temprss = new Integer(e.getValue());
                Log.d("One by one",tempmac+"::"+temprss.toString()+"One by one ");
                int flag = 0;
                for (int i = 0; i<notinroom119.size();i++)
                {
                    if(tempmac.equalsIgnoreCase(notinroom119.get(i).MAC))
                    {
                        present = false;
                        break LOOPO;
                    }
                }
                for (int i = 0;i < inroom119.size() ; i++)
                {
                    if(tempmac.equalsIgnoreCase(inroom119.get(i).MAC))
                    {
                        flag = 1;
                        if(inroom119.get(i).lb<=temprss && temprss<=inroom119.get(i).ub)
                        {
                            count++;
                            Log.d("Match found.",tempmac+"::"+temprss.toString()+"Match Found");
                            break ;
                        }
                    }
                }
                if(flag == 0)
                {
                    present = false;
                    break ;
                }
            }
            Log.d(TAG,String.valueOf(count)+"count");
            if(macFamily.size()==0)
            {
                present = false;
            }
            else if(count == macFamily.size())
            {
                present = true;
            }
            if(present)
            {
                Toast.makeText(this,"Present.",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,"Absent.",Toast.LENGTH_LONG).show();
            }

            stopService(new Intent(getBaseContext(),UserCollectWiFiData.class));
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        finally {
            try {
                wifiRSSIW.flush();
                wifiRSSIW.close();
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Toast.makeText(this,"Ended.",Toast.LENGTH_LONG).show();
    }
}
