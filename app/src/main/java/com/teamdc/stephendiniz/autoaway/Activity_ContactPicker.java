package com.teamdc.stephendiniz.autoaway;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.teamdc.stephendiniz.autoaway.classes.Contact;
import com.teamdc.stephendiniz.autoaway.classes.ContactService;
import com.teamdc.stephendiniz.autoaway.classes.MessageListArrayAdapter;
import com.teamdc.stephendiniz.autoaway.classes.PhoneContact;

import static com.teamdc.stephendiniz.autoaway.classes.Utils.*;

public class Activity_ContactPicker extends ListActivity 
{	
	private static final String	TAG = "ContactPicker";		

	Resources r;
	Dialog dialog;
	private Bundle infoBundle;

	SharedPreferences prefs;
	
	final String THEME_PREF		= "themePreference";
	
	private final int FILTER_BLACKLIST = 2;
	private final int FILTER_WHITELIST = 3;

	static final int FILTERING_ERROR_EXISTS	= 0;
	static final int FILTERING_ERROR_NUMBER	= 1;
	static final int FILTERING_ADDED		= 2;
	static final int FILTERING_SAVED		= 3;
	static final int FILTERING_BLANK		= 4;

	List<Contact> contacts = new ArrayList<Contact>();
	List<PhoneContact> pContacts = new ArrayList<PhoneContact>();

	private int filterStatus;
	private String file;
    private ContactService contactService;

    @SuppressLint("NewApi")
	public void onCreate(Bundle SavedInstanceState)
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
        contactService = new ContactService(this);

		if(android.os.Build.VERSION.SDK_INT >= 14)
		{
			if(prefs.getString(THEME_PREF, "LIGHT").equals("LIGHT"))
				setTheme(R.style.HoloLight);
			else
				setTheme(R.style.HoloDark);
		}
		
		super.onCreate(SavedInstanceState);

		if (android.os.Build.VERSION.SDK_INT >= 11)
			getActionBar().setDisplayHomeAsUpEnabled(true);
		
		r = getResources();

		infoBundle = getIntent().getExtras();
		
		setFilterType(infoBundle.getInt("extraFilterStatus"));

		switch(getFilterType()) {
			case FILTER_BLACKLIST:
				setFile("filtering_blacklist.txt");
			break;
			
			case FILTER_WHITELIST:
				setFile("filtering_whitelist.txt");
			break;
		}

		setTitle(r.getString(R.string.pref_contacts_title));

        contacts = contactService.readContactsFromFile(file);

        pContacts = contactService.getAllPhoneContacts();

		setListAdapter(new MessageListArrayAdapter(this, asListable(pContacts)));
	}

    @SuppressLint("NewApi")
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Object o = this.getListAdapter().getItem(position);

		String number = null;
		String added = null;
		String keyword = o.toString();
		String name = keyword;
        PhoneContact phoneContact = contactService.getPhoneContactByName(pContacts, name);
        String search = phoneContact.getId();

        List<Contact> contactList = phoneContact.splitInContacts();
        boolean aNewContactWasAdded = contacts.addAll(contactList);

        int count = contactList.size();

//		if (!aNewContactWasAdded)
//				showTheMessage(FILTERING_ERROR_EXISTS, number);
//		else
        if (count < 1)
			showTheMessage(FILTERING_ERROR_NUMBER,null);
		else
//        if (count > 0)
			showTheMessage(FILTERING_ADDED, name);

		contactService.sortContactsByName(contacts);
        contactService.saveContactsToFile(file, contacts);

		finish();
	}

	public void showTheMessage(int id, String extra) {
		String message;

		switch(id) {

			case FILTERING_ERROR_NUMBER:
				message = r.getString(R.string.prompt_error_filter_number);
			    break;
			
			case FILTERING_ERROR_EXISTS:
				message = r.getString(R.string.prompt_error_filter_exists);
			    break;
			
			case FILTERING_ADDED:
				message = "\'" + extra + "\'" + " " + r.getString(R.string.prompt_added);
			    break;
			
			case FILTERING_BLANK:
				message = r.getString(R.string.prompt_error_filter_blank);
			    break;

            default:
                message = "";
                break;
		}
		
		Toast eToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		eToast.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
	            Intent parentActivityIntent = new Intent(this, Activity_Filtering.class);
	            parentActivityIntent.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            parentActivityIntent.putExtra("extraFilterStatus", getFilterType());
	            startActivity(parentActivityIntent);
	            finish();
	        return true;
		}
		
		return false;
	}
	
	public int getFilterType()						{ return filterStatus; 					}
	public void setFilterType(int filterStatus)		{ this.filterStatus = filterStatus;		}
	
	public String getFile()								{ return file;							}
	public void setFile(String file)					{ this.file = file;						}
}