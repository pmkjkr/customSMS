package com.ddoobbo.fathersms.model;

import android.graphics.Bitmap;

public class Msg {
    private String id;
    private String t_id;
    private String date;
    private String dispDate;
    private String addr;
    private String contact;
    private String direction;
    private String body;
    private Bitmap img;
    private boolean bData;
    //Date vdat;

    public Msg(String ID) {
        id = ID;
        body = "";
    }

    public void setDate(String d) {
        date = d;
        dispDate = msToDate(date);
    }
    public void setThread(String d) { t_id = d; }

    public void setAddr(String a) {
        addr = a;
    }
    public void setContact(String c) {
        if (c==null) {
            contact = "Unknown";
        } else {
            contact = c;
        }
    }
    public void setDirection(String d) {
        if ("1".equals(d))
            direction = "FROM: ";
        else
            direction = "TO: ";

    }
    public void setBody(String b) {
        body = b;
    }
    public void setImg(Bitmap bm) {
        img = bm;
        if (bm != null)
            bData = true;
        else
            bData = false;
    }

    public String getDate() {
        return date;
    }
    public String getDispDate() {
        return dispDate;
    }
    public String getThread() { return t_id; }
    public String getID() { return id; }
    public String getBody() { return body; }
    public Bitmap getImg() { return img; }
    public boolean hasData() { return bData; }

    public String toString() {

        String s = id + ". " + dispDate + " - " + direction + " " + contact + " " + addr + ": "  + body;
        if (bData)
            s = s + "\nData: " + img;
        return s;
    }

    public String msToDate(String mss) {

        long time = Long.parseLong(mss,10);

        long sec = ( time / 1000 ) % 60;
        time = time / 60000;

        long min = time % 60;
        time = time / 60;

        long hour = time % 24 - 5;
        time = time / 24;

        long day = time % 365;
        time = time / 365;

        long yr = time + 1970;

        day = day - ( time / 4 );
        long mo = getMonth(day);
        day = getDay(day);

        mss = String.valueOf(yr) + "/" + String.valueOf(mo) + "/" + String.valueOf(day) + " " + String.valueOf(hour) + ":" + String.valueOf(min) + ":" + String.valueOf(sec);

        return mss;
    }
    public long getMonth(long day) {
        long[] calendar = {31,28,31,30,31,30,31,31,30,31,30,31};
        for(int i = 0; i < 12; i++) {
            if(day < calendar[i]) {
                return i + 1;
            } else {
                day = day - calendar[i];
            }
        }
        return 1;
    }
    public long getDay(long day) {
        long[] calendar = {31,28,31,30,31,30,31,31,30,31,30,31};
        for(int i = 0; i < 12; i++) {
            if(day < calendar[i]) {
                return day;
            } else {
                day = day - calendar[i];
            }
        }
        return day;
    }



}