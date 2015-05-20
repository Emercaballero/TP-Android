package com.teamdc.stephendiniz.autoaway;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teamdc.stephendiniz.autoaway.classes.Contact;
import com.teamdc.stephendiniz.autoaway.classes.ContactService;
import com.teamdc.stephendiniz.autoaway.classes.MessageListArrayAdapter;

import static com.teamdc.stephendiniz.autoaway.classes.Utils.*;

public class Activity_Filtering extends ListActivity
{
	private static final String	TAG = "Filtering";

	private int filterStatus;
	private String file;
	
	private List<Contact> contacts = new ArrayList<Contact>();

    private ContactService contactService;

	Resources r;
	Dialog dialog;
	private Bundle infoBundle;
	private Boolean isRunning = false;
	
	SharedPreferences prefs;
	
	final String THEME_PREF		= "themePreference";

	private final int FILTER_BLACKLIST = 2;
	private final int FILTER_WHITELIST = 3;

	static final int FILTERING_ERROR_EXISTS	= 0;
	static final int FILTERING_ERROR_NUMBER	= 1;
	static final int FILTERING_ADDED		= 2;
	static final int FILTERING_SAVED		= 3;
	static final int FILTERING_BLANK		= 4;

	static final int CONTEXT_MENU_EDIT		= 0;
	static final int CONTEXT_MENU_REMOVE	= 1;
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState)
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
		
		super.onCreate(savedInstanceState);

		if (android.os.Build.VERSION.SDK_INT >= 11)
			getActionBar().setDisplayHomeAsUpEnabled(true);
		
		isRunning = false;

		infoBundle = getIntent().getExtras();
		r = getResources();

		setFilterStatus(infoBundle.getInt("extraFilterStatus"));

		switch(getFilterStatus())
		{
			case FILTER_BLACKLIST:
				setFile("filtering_blacklist.txt");
				setTitle(r.getString(R.string.pref_filter_type_3));
			break;
			
			case FILTER_WHITELIST:
				setFile("filtering_whitelist.txt");
				setTitle(r.getString(R.string.pref_filter_type_4));
			break;
		}

		grabNumbers(getFile());

		setListAdapter(new MessageListArrayAdapter(this, asListable(contacts)));
		
		registerForContextMenu(getListView());
	}

	public void onResume()
	{
		super.onResume();
		
		if (isRunning)
		{
			grabNumbers(getFile());
			startActivity(getIntent());finish();
			isRunning = false;
		}



    }

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_filtering, menu);
		
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.filtering_contact_add:
				Intent intent = new Intent(this, Activity_ContactPicker.class);
				
				intent.putExtra("extraFilterStatus", getFilterStatus());
				
				startActivity(intent);
				isRunning = true;
			break;
			
			case R.id.filtering_contact_addCustom:
				dialog = new Dialog(this);
				
				dialog.setContentView(R.layout.filtering_add);
				dialog.setTitle(r.getString(R.string.prompt_filter_title));
				
				Button pButton = (Button)dialog.findViewById(R.id.dialog_filteringButtonPositive_add);
				pButton.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{
						EditText eName = (EditText)dialog.findViewById(R.id.dialog_filteringNameEdit_add);
						EditText eNumber = (EditText)dialog.findViewById(R.id.dialog_filteringNumberEdit_add);

                        String name = eName.getText().toString();
                        String number = eNumber.getText().toString();

                        if(name == null || "".equals(name) || number == null || "".equals(number)){
                            showTheMessage(FILTERING_BLANK, null);
                        } else if (contactService.contactExists(contacts, name)) {
                            showTheMessage(FILTERING_ERROR_EXISTS, null);
                        } else {
                            Contact newContact = new Contact(name.trim(), hyphenatePhoneNumber(number.trim()));
                            contacts.add(newContact);
                            showTheMessage(FILTERING_ADDED, name.trim());
                            dialog.cancel();
                            contactService.sortContactsByName(contacts);
                            saveNumbers(getFile());
                            startActivity(getIntent());
                            finish();
							}
						}
				});
				
				Button nButton = (Button)dialog.findViewById(R.id.dialog_filteringButtonNegative_add);
				nButton.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{
						dialog.cancel();
					}
				});
				
				dialog.show();
			break;
			
			case R.id.filtering_contact_removeAll:
				contacts.removeAll(contacts);
				saveNumbers(getFile());
				startActivity(getIntent());
                finish();
			break;
			case android.R.id.home:
	            Intent parentActivityIntent = new Intent(this, Activity_Main.class);
	            parentActivityIntent.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(parentActivityIntent);
	            finish();
	        return true;
		}
		
		return true;
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.setHeaderTitle(contacts.get(info.position).getName());
		String[] menuItems = {	r.getString(R.string.menu_edit),
								r.getString(R.string.menu_remove)	};

		for (int i = 0; i < menuItems.length; i++)
			menu.add(Menu.NONE, i, i, menuItems[i]);
	}
	
	public boolean onContextItemSelected(MenuItem item)
	{
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();

	  final int iId = info.position;
	  switch(menuItemIndex)
	  {
	  	case CONTEXT_MENU_EDIT:
	  		dialog = new Dialog(this);
			
			dialog.setContentView(R.layout.filtering_edit);
			dialog.setTitle(r.getString(R.string.menu_edit) + " " + contacts.get(iId).getName());
			
			Button pButton = (Button)dialog.findViewById(R.id.dialog_filteringButtonPositive_edit);
			pButton.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					EditText eName = (EditText)dialog.findViewById(R.id.dialog_filteringNameEdit_edit);
					EditText eNumber = (EditText)dialog.findViewById(R.id.dialog_filteringNumberEdit_edit);
					
					if(eName.getText().toString().equals("") || eName.getText().toString().equals(null) || eNumber.getText().toString().equals("") || eNumber.getText().toString().equals(null))
						showTheMessage(FILTERING_BLANK, null);

					else
					{
						if((contacts.get(iId).getName().equals(eName.getText().toString())) && (contacts.get(iId).getNumber().equals(hyphenatePhoneNumber(eNumber.getText().toString()))))
							dialog.cancel();

						else
						{
							contacts.get(iId).setInfo(eName.getText().toString().trim(), hyphenatePhoneNumber(eNumber.getText().toString().trim()));
							showTheMessage(FILTERING_SAVED, eName.getText().toString().trim());
							dialog.cancel();
							saveNumbers(getFile());
							
							Log.i(TAG, "\"" + contacts.get(iId).getName() + "\" edited successfully");
							startActivity(getIntent()); finish();
						}
					}
				}
			});
			
			Button nButton = (Button)dialog.findViewById(R.id.dialog_filteringButtonNegative_edit);
			nButton.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					dialog.cancel();
				}
			});
			
			dialog.show();
			pButton.setText(r.getString(R.string.menu_save));
			EditText eTitle = (EditText)dialog.findViewById(R.id.dialog_filteringNameEdit_edit);
			eTitle.setText(contacts.get(iId).getName());
			EditText eContent = (EditText)dialog.findViewById(R.id.dialog_filteringNumberEdit_edit);
			eContent.setText(dehyphenate(contacts.get(iId).getNumber()));
	  	break;
	  	case CONTEXT_MENU_REMOVE:
	  		contacts.remove(info.position);
	  		saveNumbers(getFile());
			startActivity(getIntent()); finish();
	  	break;
	  }
	  return true;
	}

	public boolean grabNumbers(String file) {
		contacts.clear();

		try {
			File inFile = getBaseContext().getFileStreamPath(getFile());

			if (inFile.exists()) {
				InputStream iStream = openFileInput(getFile());
				InputStreamReader iReader = new InputStreamReader(iStream);
				BufferedReader bReader = new BufferedReader(iReader);

				String line;
				//Should be in groups of TWO!
				while((line = bReader.readLine()) != null) {
					Contact contactFromFile = new Contact(line, bReader.readLine());
					contacts.add(contactFromFile);
				}

				iStream.close();

				Log.i(TAG, contacts.size() + " contacts(s) read from file");
			}
			else
				Log.w(TAG, "\"" + getFile() + "\" was not found!");
		}
		catch (java.io.FileNotFoundException exception) { Log.e(TAG, "FileNotFoundException caused by openFileInput(fileName)", exception); }
		catch (IOException exception) 					{ Log.e(TAG, "IOException caused by buffreader.readLine()", exception); 			}

		return !contacts.isEmpty();
	}
	
	public void saveNumbers(String file){
        OutputStreamWriter oWriter = null;
        try{

            oWriter = new OutputStreamWriter(openFileOutput(file, 0));

            for(Contact contact : contacts){
                oWriter.append(contact.getName() + "\n");
                oWriter.append(contact.getNumber() + "\n");
            }

		} catch (IOException exception) {
            Log.e(TAG, "IOException caused by trying to access " + file, exception);
        } finally {

            try {

                if(oWriter != null){
                    oWriter.flush();
                    oWriter.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException caused by trying to close " + file, e);
            }

        }
	}
	
	public void showTheMessage(int id, String extra)
	{
		String message = "";

		switch(id)
		{
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
			
			case FILTERING_SAVED:
				message = "\'" + extra + "\'" + " " + r.getString(R.string.prompt_message_saved);
			break;
		}
		
		Toast eToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		eToast.show();
	}

	public int getFilterStatus()						{ return filterStatus; 					}
	public void setFilterStatus(int filterStatus)		{ this.filterStatus = filterStatus;		}
	
	public String getFile()								{ return file;							}
	public void setFile(String file)					{ this.file = file;						}
}
