package com.chinalwb.contactandsms;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_PHONE_NUMBER = 1;

    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 2;

    private static final int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 3;


    private static final Uri URI_DATA     = android.provider.ContactsContract.Data.CONTENT_URI;

    private RecyclerView contact_recycle_view;

    private List<ContactModel> mContactsList = new ArrayList<>();

    private RecyclerView.Adapter mAdapter = new ContactAdapter(mContactsList);;

    private ContactModel mContactModelTmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectContact();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        contact_recycle_view = findViewById(R.id.contact_recycle_view);
        // initContactsList();
    }

    private void selectContact() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_PHONE_NUMBER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                selectContact();
            }
        }
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                sendSms();
            }
        }

        if (requestCode == READ_PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                sendSms();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            afterSelectContact(data);
        }
    }


    private void afterSelectContact(Intent intent) {
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                , ContactsContract.CommonDataKinds.Phone.NUMBER
                , ContactsContract.CommonDataKinds.Phone.PHOTO_ID
                , ContactsContract.CommonDataKinds.Phone.PHOTO_FILE_ID
                , ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
                , ContactsContract.CommonDataKinds.Phone.PHOTO_URI};

        // Get the URI and query the content provider for the phone number
        Uri contactUri = intent.getData();
        if (contactUri == null) {
            return;
        }

        try (Cursor cursor = getContentResolver().query(
                contactUri,
                projection,
                null,
                null,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int displayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int photoIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
                int photoFileIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_FILE_ID);
                int photoThumbnailUriIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
                int photoUriIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                String number = cursor.getString(numberIndex);
                String contact_id = "";
                String display_name = "";
                String first_name = "";
                String last_name = "";
                String photo_id = "";
                String photo_file_id = "";
                String photo_thumbnail_uri = "";
                String photo_uri = "";
                if (contactIdIndex > -1) {
                    contact_id = cursor.getString(contactIdIndex);
                }
                String[] firstNameAndLastName = getFirstNameAndLastName(contact_id);
                if (firstNameAndLastName != null && firstNameAndLastName.length == 2) {
                    first_name = firstNameAndLastName[0];
                    last_name = firstNameAndLastName[1];
                }
                if (displayNameIndex > 0) {
                    display_name = cursor.getString(displayNameIndex);
                }
                if (photoIdIndex > 0) {
                    photo_id = cursor.getString(photoIdIndex);
                }
                if (photoFileIdIndex > 0) {
                    photo_file_id = cursor.getString(photoFileIdIndex);
                }
                if (photoThumbnailUriIndex > 0) {
                    photo_thumbnail_uri = cursor.getString(photoThumbnailUriIndex);
                }
                if (photoUriIndex > 0) {
                    photo_uri = cursor.getString(photoUriIndex);
                }

                // Do something with the phone number
                Log.i("XX", "onActivityResult() called with: "
                        + "number = [" + number
                        + "], display_name = [" + display_name
                        + "], first_name = [" + first_name
                        + "], last_name = [" + last_name
                        + "], contact_id = [" + contact_id
                        + "], photo_id = [" + photo_id
                        + "], photo_file_id = [" + photo_file_id
                        + "], photo_thumbnail_uri = [" + photo_thumbnail_uri
                        + "], photo_uri = [" + photo_uri + "]");

                mContactModelTmp = new ContactModel();
                mContactModelTmp.setDisplay_name(display_name);
                mContactModelTmp.setFirst_name(first_name);
                mContactModelTmp.setLast_name(last_name);
                mContactModelTmp.setNumber(number);
                mContactModelTmp.setPhoto_uri(photo_uri);
                mContactModelTmp.setPhoto_thumbnail_uri(photo_thumbnail_uri);
                mContactsList.add(mContactModelTmp);
                showBottomSheetDialog();
            }
        }
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        EditText editText = dialog.findViewById(R.id.sms_content);
        editText.setText("Hello: " + mContactModelTmp.getNumber() + ", " + mContactModelTmp.getDisplay_name());
        dialog.setCanceledOnTouchOutside(false);
        TextView send = dialog.findViewById(R.id.send);
        assert send != null;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void sendSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.SEND_SMS
                            // , Manifest.permission.READ_PHONE_STATE
                    },
                    SEND_SMS_PERMISSION_REQUEST_CODE);
            return;
        } else {

            final String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED), 0);

            SmsManager sms = SmsManager.getDefault();
            String message = "Just a test message!";
            try {
                sms.sendTextMessage(mContactModelTmp.getNumber(), null, message, sentPI, deliveredPI);
                updateUI();
//            sms.sendTextMessage("+8618500529597", null, message, sentPI, deliveredPI);
            } catch (Exception e) {
                e.printStackTrace();
                if (Build.VERSION.SDK_INT == 26) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{
                                        Manifest.permission.READ_PHONE_STATE
                                },
                                READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        }
    }

    private String[] getFirstNameAndLastName(String contactId) {
        Cursor nameCursor = null;
        String[] firstNameAndLastName = new String[2];
        try {
            String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] nameWhereParams = new String[] { contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
            nameCursor = getContentResolver().query(URI_DATA, null, nameWhere, nameWhereParams, null);
            if (nameCursor != null && nameCursor.moveToNext()) {
                String firstName = nameCursor.getString(nameCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                String lastName = nameCursor.getString(nameCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                firstNameAndLastName[0] = firstName;
                firstNameAndLastName[1] = lastName;
            }
        } finally {
            if (nameCursor != null) {
                nameCursor.close();
            }
        }
        return firstNameAndLastName;
    }

    private void updateUI() {
        if (contact_recycle_view.getAdapter() == null) {
            contact_recycle_view.setAdapter(mAdapter);
            contact_recycle_view.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }
}
