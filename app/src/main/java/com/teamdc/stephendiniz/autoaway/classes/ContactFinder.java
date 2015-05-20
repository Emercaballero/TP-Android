package com.teamdc.stephendiniz.autoaway.classes;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.teamdc.stephendiniz.autoaway.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.teamdc.stephendiniz.autoaway.classes.Utils.*;

/**
 * Created by sscotti on 5/19/15.
 */
public class ContactFinder {

    private Context context;

    private final String MULTIPLE_NUMBERS;

    public ContactFinder(Context context){
        this.context = context;
        MULTIPLE_NUMBERS = context.getResources().getString(R.string.pref_contacts_multiple);
    }

    public List<PhoneContact> getAllPhoneContacts(){

        List<PhoneContact> phoneContacts = new ArrayList<PhoneContact>();

        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = null;
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String sortOrder = null;

        //Checks phoneContacts for the number passed (returnAddress)
        while (cursor.moveToNext())	{

            int num = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (num > 0) {

                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                PhoneContact phoneContact = new PhoneContact(name, id);

                String[] selectionArgs = {id};

                Cursor phone = context.getContentResolver().query(contentUri, projection, selection, selectionArgs, sortOrder);

                for (phone.moveToFirst(); !phone.isAfterLast(); phone.moveToNext()){

                    String phoneNumber = hyphenatePhoneNumber(phone.getString(phone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    phoneContact.addPhoneNumber(phoneNumber);

                }

                phone.close();

                phoneContacts.add(phoneContact);
            }

        }

        cursor.close();

        sortPhoneContactsByName(phoneContacts);

        return phoneContacts;

    }

    public PhoneContact getByName(List<PhoneContact> contacts, String name){

        for(PhoneContact contact : contacts){
            if(contact.getName().equals(name)){
                return contact;
            }
        }

        return null;
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

}
