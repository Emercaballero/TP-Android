package com.teamdc.stephendiniz.autoaway.classes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamdc.stephendiniz.autoaway.R;

import java.util.List;

public class MessageListPhoneContactArrayAdapter extends ArrayAdapter<PhoneContact> {

    private Activity context;
    private List<PhoneContact> phoneContacts;

    public MessageListPhoneContactArrayAdapter(Activity context, List<PhoneContact> contacts){
        super(context, R.layout.message_list, contacts);

        this.context = context;
        this.phoneContacts = contacts;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.message_list, null, true);
		
		TextView textTitle = (TextView) rowView.findViewById(R.id.dl_Title);
		TextView textContent = (TextView) rowView.findViewById(R.id.dl_Content);

        PhoneContact phoneContact = phoneContacts.get(position);
        textTitle.setText(phoneContact.getName());

        if(phoneContact.hasMultipleNumbers()){
            textContent.setText(context.getResources().getString(R.string.pref_contacts_multiple));
        } else {
            textContent.setText(phoneContact.getNumber());
        }

		return rowView;
	}
}