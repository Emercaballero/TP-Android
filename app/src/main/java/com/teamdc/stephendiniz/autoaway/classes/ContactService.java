package com.teamdc.stephendiniz.autoaway.classes;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.teamdc.stephendiniz.autoaway.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.teamdc.stephendiniz.autoaway.classes.Utils.*;

/**
 * Created by sscotti on 5/19/15.
 */
public class ContactService {

    private static final String TAG = "ContactService";
    private Context context;

    private final String MULTIPLE_NUMBERS;

    public ContactService(Context context){
        this.context = context;
        MULTIPLE_NUMBERS = context.getResources().getString(R.string.pref_contacts_multiple);
    }

    public List<PhoneContact> getAllPhoneContacts(){

        List<PhoneContact> phoneContacts = new ArrayList<PhoneContact>();

        Cursor contactsCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = null;
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String sortOrder = null;

        //Checks phoneContacts for the number passed (returnAddress)
        while (contactsCursor.moveToNext())	{

            int num = contactsCursor.getInt(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (num > 0) {

                String name = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String id = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                PhoneContact phoneContact = new PhoneContact(name, id);

                String[] selectionArgs = {id};

                Cursor phoneCursor = context.getContentResolver().query(contentUri, projection, selection, selectionArgs, sortOrder);

                for (phoneCursor.moveToFirst(); !phoneCursor.isAfterLast(); phoneCursor.moveToNext()){

                    String phoneNumber = hyphenatePhoneNumber(phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    phoneContact.addPhoneNumber(phoneNumber);

                }

                phoneCursor.close();

                phoneContacts.add(phoneContact);
            }

        }

        contactsCursor.close();

        sortPhoneContactsByName(phoneContacts);

        return phoneContacts;

    }

    public PhoneContact getPhoneContactByName(List<PhoneContact> contacts, String name){

        for(PhoneContact contact : contacts){
            if(contact.getName().equals(name)){
                return contact;
            }
        }

        return null;
    }

    public boolean phoneContactExists(List<PhoneContact> phoneContacts, String name){
        return this.getPhoneContactByName(phoneContacts, name) != null;
    }

    public Contact getContactByName(List<Contact> contacts, String name){

        for(Contact contact : contacts){
            if(contact.getName().equals(name)){
                return contact;
            }
        }

        return null;
    }

    public boolean contactExists(List<Contact> contacts, String name){
        return this.getContactByName(contacts, name) != null;
    }

    public void sortPhoneContactsByName(List<PhoneContact> contacts){
        Collections.sort(contacts, new Comparator<PhoneContact>() {
            public int compare(PhoneContact lhs, PhoneContact rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }

    public void sortContactsByName(List<Contact> contacts){
        Collections.sort(contacts, new Comparator<Contact>() {
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }

    public List<Contact> readContactsFromFile(String filePath) {

        List<Contact> contacts = new ArrayList<Contact>();

        File inFile = context.getFileStreamPath(filePath);

        if (inFile.exists()) {
            try {

                contacts = readFile(filePath, context.openFileInput(filePath), new RegisterDeserializer<Contact>() {
                    public Contact deserialize(String line) {
                        String[] split = line.split(",");
                        return new Contact(split[0], split[1]);
                    }
                });

            } catch (java.io.FileNotFoundException exception) {
                Log.e(TAG, "FileNotFoundException caused by " + filePath, exception);
            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by buffreader.readLine()", exception);
            }

        }

        return contacts;
    }

    public void saveContactsToFile(String filePath, List<Contact> contacts){
        try {
            saveToFile(filePath, context.openFileOutput(filePath, 0), contacts, new RegisterSerializer<Contact>() {
                public String serialize(Contact object) {
                    return String.format("%s,%s", object.getName(), object.getNumber());
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "IOException caused by trying to access " + filePath, e);
        }
    }

}
