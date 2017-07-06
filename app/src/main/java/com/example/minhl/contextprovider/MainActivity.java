package com.example.minhl.contextprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.*;

public class MainActivity extends AppCompatActivity {
    private final static String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private final static Uri URI_CONTENT = ContactsContract.Contacts.CONTENT_URI;
    private final static String _ID = ContactsContract.Contacts._ID;
    private final static String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private final static Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private final static String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private final static String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final int REQUEST_PERMISSIONS = 100;

    private Cursor cursor;
    private ListView lvContact;
    private List<String> listContact;
    private Button btnShowContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initALotOfThings();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }
        if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode);
        } else {
            Toast.makeText(this, "Ko cấp quyền sao chạy dc :(", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void onPermissionsGranted(int requestCode) {
        Toast.makeText(this, "Ahihi đồ ngốc", Toast.LENGTH_SHORT).show();
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
            String contactID = getCussorStringWithColum(cursor, _ID);
            String name = getCussorStringWithColum(cursor, DISPLAY_NAME);
            int hasphoneNumber = Integer.parseInt(getCussorStringWithColum(cursor,
                            HAS_PHONE_NUMBER));
                    if (isAvailable(hasphoneNumber)) {
                        element.append(contactID).append(". First Name: ").append(name);
                        Cursor phoneCursor = getContentResolver().query(PhoneCONTENT_URI, null,
                                Phone_CONTACT_ID + " = ?", new String[]{contactID}, null); //vẫn chưa hiểu làm câu lệnh query này
                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = getCussorStringWithColum(phoneCursor, NUMBER);
                    element.append("\n    Phone number: " + phoneNumber);
                }
                phoneCursor.close();
            }
            listContact.add(element.toString());
        }
        cursor.close();
        setAdapter();
    }

    public String getCussorStringWithColum(Cursor cursor, String index) {
        return cursor.getString(cursor.getColumnIndex(index));
    }

    private void initALotOfThings() {
        btnShowContact = (Button) findViewById(R.id.btn_show_contact);
        lvContact = (ListView) findViewById(R.id.lv);
        listContact = new ArrayList<>();
        askForPermission();
        btnShowContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContact();
            }
        });
    }

    private void askForPermission() {
        String[] PERMISSION = {READ_CONTACTS};
        if (!UtilPermissions.hasPermissions(this,PERMISSION)) {
            ActivityCompat.requestPermissions(this, PERMISSION, REQUEST_PERMISSIONS);
        }
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
