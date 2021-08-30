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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
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

        ArrayList<SmsInfo> smsList = new ArrayList<>();

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
                smsList.add(sms);
            }
        }

        RecyclerView smsRv = findViewById(R.id.sms_list);
        smsRv.setLayoutManager(new LinearLayoutManager(this));

        SmsAdapter smsAdapter = new SmsAdapter();
        smsAdapter.updateItems(smsList);
        smsRv.setAdapter(smsAdapter);
    }
}