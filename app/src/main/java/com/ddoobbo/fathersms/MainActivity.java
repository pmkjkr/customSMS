package com.ddoobbo.fathersms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ddoobbo.fathersms.model.Msg;
import com.ddoobbo.fathersms.model.SmsInfo;
import com.ddoobbo.fathersms.model.StSms;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        scanMMS();
                        scanSMS();
//                        readSMSMessage();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setRationaleMessage("SMS 읽기 권한")
                .setPermissions(Manifest.permission.READ_SMS)
                .check();
    }

    public void readSMSMessage() {
        Uri allMessage = Uri.parse("content://sms");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(allMessage, new String[] { "_id", "thread_id", "address", "person", "date", "body" }, null, null, "date DESC");

        String string = "";
        int count = 0;

        ArrayList<StSms> stSmsList = new ArrayList<>();

        String stAddress = "";

        // _id, date, _data, text, address


        while (c.moveToNext()) {
            String address = c.getString(2);


            if(!address.contains("1544")){
                long messageId = c.getLong(0);
                long threadId = c.getLong(1);
                long contactId = c.getLong(3);
                String contactId_string = String.valueOf(contactId);


//
                long timestamp = c.getLong(4);
                String body = c.getString(5);
//                long timestamp = c.getLong(1);
//                String body = c.getString(4);

//                string = String.format("msgid:%d, threadid:%d, address:%s, " + "contactid:%d, contackstring:%s, timestamp:%d, body:%s", messageId, threadId, address, contactId,
//                        contactId_string, timestamp, body);

                Log.d("text", ++count + "st, Message: " + body+", address: "+address+", timestamp: "+timestamp);

                SmsInfo sms = new SmsInfo(timestamp, address, body);

                if(!address.equals(stAddress)) {
                    stAddress = address;
                    ArrayList<SmsInfo> s = new ArrayList<>();
                    s.add(sms);
                    stSmsList.add(new StSms(stAddress, timestamp, s));
                } else {
                    StSms st = stSmsList.get(stSmsList.size()-1);
                    if(st.getLatestTimestamp() < timestamp)
                        st.setLatestTimestamp(timestamp);

                    st.getSmsList().add(sms);
                }
            }
        }

        for (int i = 0; i < stSmsList.size(); i++) {
            for (int j = 0; j < stSmsList.get(i).getSmsList().size(); j++) {
                Collections.sort(stSmsList.get(i).getSmsList(), new Comparator<SmsInfo>() {
                    @Override
                    public int compare(SmsInfo o1, SmsInfo o2) {
                        if(o1.getTimestamp() > o2.getTimestamp()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
            }
        }

        for (int i = 0; i < stSmsList.size(); i++) {
            Collections.sort(stSmsList, new Comparator<StSms>() {
                @Override
                public int compare(StSms o1, StSms o2) {
                    if(o1.getLatestTimestamp() > o2.getLatestTimestamp()){
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        }

        RecyclerView smsRv = findViewById(R.id.sms_list);
        smsRv.setLayoutManager(new LinearLayoutManager(this));

        SmsAdapter smsAdapter = new SmsAdapter();
        smsAdapter.updateItems(stSmsList);
        smsRv.setAdapter(smsAdapter);
    }






    @SuppressLint("Range")
    public void scanSMS() {
        System.out.println("==============================ScanSMS()==============================");
        //Initialize Box
        Uri uri = Uri.parse("content://sms");
        String[] proj = {"*"};
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(uri,proj,null,null,null);

        if(c.moveToFirst()) {
            do {
                String[] col = c.getColumnNames();
                String str = "";
                for(int i = 0; i < col.length; i++) {
                    str = str + col[i] + ": " + c.getString(i) + ", ";
                }
                //System.out.println(str);

                System.out.println("--------------------SMS------------------");

                @SuppressLint("Range") Msg msg = new Msg(c.getString(c.getColumnIndex("_id")));
                msg.setDate(c.getString(c.getColumnIndex("date")));
                msg.setAddr(c.getString(c.getColumnIndex("Address")));
                msg.setBody(c.getString(c.getColumnIndex("body")));
                msg.setDirection(c.getString(c.getColumnIndex("type")));
                msg.setContact(c.getString(c.getColumnIndex("person")));
                System.out.println(msg);


            } while (c.moveToNext());
        }
        c.close();
    }

    @SuppressLint("Range")
    public void scanMMS() {
        System.out.println("==============================ScanMMS()==============================");
        //Initialize Box
        Uri uri = Uri.parse("content://mms");
        String[] proj = {"*"};
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(uri, proj, null, null, null);
        Log.e("MainActivity", "scanMMS1");
        int jj = 0;
        if(c.moveToFirst()) {
            do {
                /*String[] col = c.getColumnNames();
                String str = "";
                for(int i = 0; i < col.length; i++) {
                    str = str + col[i] + ": " + c.getString(i) + ", ";
                }
                System.out.println(str);*/
                //System.out.println("--------------------MMS------------------");
                @SuppressLint("Range") Msg msg = new Msg(c.getString(c.getColumnIndex("_id")));
                msg.setThread(c.getString(c.getColumnIndex("thread_id")));
                msg.setDate(c.getString(c.getColumnIndex("date")));
                msg.setAddr(getMmsAddr(msg.getID()));

                Log.e("MainActivity", "scanMMS2~ jj::"+jj);
                ParseMMS(msg);
                jj++;
            } while (c.moveToNext());
        }

        c.close();

    }

    @SuppressLint("Range")
    public void ParseMMS(Msg msg) {
        Uri uri = Uri.parse("content://mms/part");
        String mmsId = "mid = " + msg.getID();
        Cursor c = getContentResolver().query(uri, null, mmsId, null, null);
        int one = 0;
        while(c.moveToNext() && one == 0) {
/*          String[] col = c.getColumnNames();
            String str = "";
            for(int i = 0; i < col.length; i++) {
                str = str + col[i] + ": " + c.getString(i) + ", ";
            }
            System.out.println(str);*/

            @SuppressLint("Range") String pid = c.getString(c.getColumnIndex("_id"));
            @SuppressLint("Range") String type = c.getString(c.getColumnIndex("ct"));
            if ("text/plain".equals(type)) {
//                msg.setBody(msg.getBody() + c.getString(c.getColumnIndex("text")));
                msg.setBody(msg.getBody());
                Log.e("MainActivity", "scanMMS3~ jj::"+msg.getBody());
            }
//            else if (type.contains("image")) {
//                msg.setImg(getMmsImg(pid));
//            }
            one++;
        }
        c.close();
    }

    public Bitmap getMmsImg(String id) {
        Uri uri = Uri.parse("content://mms/part/" + id);
        InputStream in = null;
        Bitmap bitmap = null;

        try {
            in = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(in);
            if(in != null)
                in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public String getMmsAddr(String id) {
        String sel = new String("msg_id=" + id);
        String uriString = MessageFormat.format("content://mms/{0}/addr", id);
        Uri uri = Uri.parse(uriString);
        Cursor c = getContentResolver().query(uri, null, sel, null, null);
        String name = "";
        while (c.moveToNext()) {
/*          String[] col = c.getColumnNames();
            String str = "";
            for(int i = 0; i < col.length; i++) {
                str = str + col[i] + ": " + c.getString(i) + ", ";
            }
            System.out.println(str);*/
            @SuppressLint("Range") String t = c.getString(c.getColumnIndex("address"));
            if(!(t.contains("insert")))
                name = name + t + " ";
        }
        c.close();
        return name;
    }
}