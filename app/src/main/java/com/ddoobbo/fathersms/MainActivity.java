package com.ddoobbo.fathersms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ddoobbo.fathersms.model.SmsInfo;
import com.ddoobbo.fathersms.model.StSms;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

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
                        readSMSMessage();
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
        while (c.moveToNext()) {
            String address = c.getString(2);


            if(!address.contains("1544")){
                long messageId = c.getLong(0);
                long threadId = c.getLong(1);
                long contactId = c.getLong(3);
                String contactId_string = String.valueOf(contactId);
                long timestamp = c.getLong(4);
                String body = c.getString(5);

                string = String.format("msgid:%d, threadid:%d, address:%s, " + "contactid:%d, contackstring:%s, timestamp:%d, body:%s", messageId, threadId, address, contactId,
                        contactId_string, timestamp, body);

                Log.d("heylee", ++count + "st, Message: " + body+", address: "+address+", timestamp: "+timestamp);

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
}