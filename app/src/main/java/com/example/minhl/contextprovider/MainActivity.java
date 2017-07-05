package com.example.minhl.contextprovider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private final static Uri URI_CONTENT = ContactsContract.Contacts.CONTENT_URI;
    private final static String _ID = ContactsContract.Contacts._ID;
    private final static String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private final static Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private final static String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private final static String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    Cursor cursor;
    private ListView lvContact;
    public List<String> listContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvContact = (ListView) findViewById(R.id.lv);
        listContact = new ArrayList<>();
        getContact();
    }

    public void getContact() {

        cursor = getContentResolver().query(URI_CONTENT, null, null, null, null);
        if (cursor == null) {
            return;
        }
        if (cursor.getCount() == 0) {
            return;
        }
        StringBuffer element;
        while (cursor.moveToNext()) {
            element = new StringBuffer();
            String contactID = cursor.getString(cursor.getColumnIndex(_ID));
            String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
            int hasphoneNumber = Integer.parseInt(cursor.getString
                    (cursor.getColumnIndex(HAS_PHONE_NUMBER)));
            if (isAvailable(hasphoneNumber)) {
                element.append(contactID).append(". First Name: ").append(name);

                Cursor phoneCursor = getContentResolver().query(PhoneCONTENT_URI, null,
                        Phone_CONTACT_ID + " = ?", new String[]{contactID}, null); //vẫn chưa hiểu làm câu lệnh query này
                while (phoneCursor.moveToNext()) {
                    String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    element.append("\n    Phone number: " + phoneNumber);
                }
                phoneCursor.close();
            }
            listContact.add(element.toString());
        }
        cursor.close();
        setAdapter();
    }

    public void setAdapter() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listContact);
        lvContact.setAdapter(arrayAdapter);
    }

    public boolean isAvailable(int phoneNumber) {
        return phoneNumber > 0;
    }
}
