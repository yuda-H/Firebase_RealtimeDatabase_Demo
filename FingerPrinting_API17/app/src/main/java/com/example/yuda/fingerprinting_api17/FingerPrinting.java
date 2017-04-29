package com.example.yuda.fingerprinting_api17;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class FingerPrinting extends AppCompatActivity {
    TextView txt_value;

    FirebaseDatabase mdatabase;
    DatabaseReference mRef;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    double arr[][][];


    String b1 = "c8:3a:35:28:56:b0", b2 = "00:a2:89:00:d9:61", b3 = "1c:b7:2c:ed:b5:f8", b4 = "c8:3a:35:14:ce:a0";

    //double signal0 = -69,signal1 = -74,signal2 = -51,signal3 = -83;
    /*  base1=c8:3a:35:28:56:b0      base2=00:a2:89:00:d9:61
        base3=1c:b7:2c:ed:b5:f8      base4=c8:3a:35:14:ce:a0 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_printing);
        txt_value = (TextView) findViewById(R.id.txt_value);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }




    public void btn_data(View view) {
        txt_value.setText("請稍後");
        //print = "假設待測位置之訊號強度為：\n"+signal0+"  "+signal1+"  "+signal2+"  "+signal3+"\n\n";
        mfirebase get = new mfirebase(); //use OO to call programs
        get.test();
    }



    public void btn_wifi(View view) {
        String sss = "";
        List<ScanResult> resultList = mWifiManager.getScanResults();
        ArrayList<Integer> put = new ArrayList<>();
        try {
            for (int i = 0; i < resultList.size(); i++) {
                ScanResult result = resultList.get(i);
                sss += " - \n" + result.SSID+"\n"+result.BSSID+"\b\b\b"+result.level+"\n";
            }

        } catch (Exception e) { //這是發生例外執行
            e.printStackTrace();
        }

        txt_value.setText(put.toString()+"\n"+sss);

    }


    class mfirebase {
        private String print = "";
        private double signal0 = -69,signal1 = -74,signal2 = -51,signal3 = -83;
        private void test() {
            mdatabase = FirebaseDatabase.getInstance();
            mRef = mdatabase.getReference("place");
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    arr = new double[(int)dataSnapshot.getChildrenCount()][(int)(dataSnapshot.child("place1").child("base1").getChildrenCount()+1)][(int)dataSnapshot.child("place1").getChildrenCount()];
                    for(int i=1; i<=dataSnapshot.getChildrenCount(); i++) { //place
                        for(int j=1; j<=dataSnapshot.child("place"+i).getChildrenCount(); j++) {    //base
                            for(int k=0; k<=2; k++) {
                                double mean = Double.parseDouble(dataSnapshot.child("place"+i).child("base"+j).child("mean").getValue()+"");
                                double dev = Double.parseDouble(dataSnapshot.child("place"+i).child("base"+j).child("dev").getValue()+"");
                                if(k==0) {
                                    arr[i-1][k][j-1] = putD(mean);
                                }
                                else if (k==1) {
                                    arr[i-1][k][j-1] = putD(dev);
                                }
                                else {
                                    switch (j-1) {
                                        case 3:
                                            arr[i-1][k][j-1] = putD(probability(signal3,mean,dev)); break;
                                        case 2:
                                            arr[i-1][k][j-1] = putD(probability(signal2,mean,dev)); break;
                                        case 1:
                                            arr[i-1][k][j-1] = putD(probability(signal1,mean,dev)); break;
                                        case 0:
                                            arr[i-1][k][j-1] = putD(probability(signal0,mean,dev)); break;
                                    }
                                }
                            }
                            //Log.v("get",dataSnapshot.child("place"+i).getKey()+"\tmean: \t"+dataSnapshot.child("place"+i).child("base"+j).child("mean").getValue());
                            //Log.v("get",dataSnapshot.child("place"+i).getKey()+"\tdev: \t"+dataSnapshot.child("place"+i).child("base"+j).child("dev").getValue());
                        }
                    }

                    for(int i=1; i<=dataSnapshot.getChildrenCount(); i++) { //place

                        print += "place"+i+"\n";
                        for (int k = 0; k <= 2; k++) {     // j=1 2 3 4    k=0 1 2
                            for (int j = 1; j <= dataSnapshot.child("place" + i).getChildrenCount(); j++) {
                                print+=arr[i-1][k][j-1]+"\t\t\t";
                            }
                            print+="\n";
                        }
                        print += "\n";
                    }
                    txt_value.setText(print);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //顯示到小數第n位
    private double putD(double i) {
        return (long)(i*10000)/10000.0;
    }


    //機率公式 P(s|r), 在 s 已發生的機率下 r 再發生的機率
    double probability(double s, double mean, double dev) {
        double e = Math.exp(1);
        double pi = Math.PI;
        //機率為P
        double P = Math.pow(e, -Math.pow(s - mean, 2) / (2 * Math.pow(dev, 2))) / Math.sqrt(2 * pi * Math.pow(dev, 2));
        return P;
    }


    private void timeCounter() {
        final Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }
}
