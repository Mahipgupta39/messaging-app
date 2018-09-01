package com.androstock.smsapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by SHAJIB on 7/10/2017.
 */

public class Function {


    static final String _ID = "_id";
    static final String KEY_THREAD_ID = "thread_id";
    static final String KEY_NAME = "name";
    static final String KEY_PHONE = "phone";
    static final String KEY_MSG = "msg";
    static final String KEY_TYPE = "type";
    static final String KEY_TIMESTAMP = "timestamp";
    static final String KEY_TIME = "time";


    public static  boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String converToTime(String timestamp)
    {
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm");
        return formatter.format(date);
    }


    public static HashMap<String, String> mappingInbox(String _id, String thread_id, String name, String phone, String msg, String type, String timestamp, String time)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(_ID, _id);
        map.put(KEY_THREAD_ID, thread_id);
        map.put(KEY_NAME, name);
        map.put(KEY_PHONE, phone);
        map.put(KEY_MSG, msg);
        map.put(KEY_TYPE, type);
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_TIME, time);
        return map;
    }


    public static  ArrayList<HashMap<String, String>> removeDuplicates( ArrayList<HashMap<String, String>> smsList)
    {
        ArrayList<HashMap<String, String>> gpList = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i<smsList.size(); i++)
        {
            boolean available = false;
            for (int j = 0; j<gpList.size(); j++)
            {
                if( Integer.parseInt(gpList.get(j).get(KEY_THREAD_ID)) == Integer.parseInt(smsList.get(i).get(KEY_THREAD_ID)))
                {
                    available = true;
                    break;
                }
            }

            if(!available)
            {
                gpList.add(mappingInbox(smsList.get(i).get(_ID), smsList.get(i).get(KEY_THREAD_ID),
                        smsList.get(i).get(KEY_NAME), smsList.get(i).get(KEY_PHONE),
                        smsList.get(i).get(KEY_MSG), smsList.get(i).get(KEY_TYPE),
                        smsList.get(i).get(KEY_TIMESTAMP), smsList.get(i).get(KEY_TIME)));
            }
        }
        return gpList;
    }




    public static boolean sendSMS(String toPhoneNumber, String smsMessage) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





    public static String getContactbyPhoneNumber(Context c, String phoneNumber) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = c.getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            return phoneNumber;
        }else {
            String name = phoneNumber;
            try {

                if (cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                }

            } finally {
                cursor.close();
            }

            return name;
        }
    }










    public static void createCachedFile (Context context, String key, ArrayList<HashMap<String, String>> dataList) throws IOException {
            FileOutputStream fos = context.openFileOutput (key, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject (dataList);
            oos.close ();
            fos.close ();
    }

    public static Object readCachedFile (Context context, String key) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput (key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject ();
        return object;
    }
}
